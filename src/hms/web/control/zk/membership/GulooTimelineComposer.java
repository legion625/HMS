package hms.web.control.zk.membership;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;

import hms.membership.bpu.GulooStampBuilder1;
import hms.membership.bpu.MbrBpuType;
import hms.web.zk.HmsMessageBox;
import hms.web.zk.HmsNotification;
import hms_kernel.membership.Entity;
import hms_kernel.membership.GulooStamp;
import hms_kernel.membership.GulooStampCate;
import hms_kernel.membership.MembershipService;
import hms_kernel.membership.type.EntityType;
import legion.BusinessServiceFactory;
import legion.biz.BpuFacade;

/**
 * 咕溜時光軸(電腦版 prototype)。
 * 畫面、篩選與快速輸入解析都在 gulooTimeline.js；這裡只負責提供資料，以及處理新增(onQuickAdd)、刪除(onDeleteStamp)。
 */
public class GulooTimelineComposer extends SelectorComposer<Component> {
	private Logger log = LoggerFactory.getLogger(getClass());

	private MembershipService mbrService = BusinessServiceFactory.getInstance().getService(MembershipService.class);

	private Component root;

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		root = comp;
		refresh(null, false, false);
	}

	// -------------------------------------------------------------------------------
	@Listen("onQuickAdd = #divGuloo")
	public void quickAdd(Event _evt) {
		Map<?, ?> d = (Map<?, ?>) _evt.getData();

		long stampDate;
		try {
			stampDate = LocalDate.parse(String.valueOf(d.get("date"))).atStartOfDay(ZoneId.systemDefault()).toInstant()
					.toEpochMilli();
		} catch (DateTimeParseException e) {
			HmsMessageBox.exclamation("日期格式不正確。");
			return;
		}

		Map<String, Entity> entityMap = toMap(mbrService.loadEntityList(), Entity::getUid);
		Map<String, GulooStampCate> cateMap = toMap(mbrService.loadGulooStampCateList(), GulooStampCate::getUid);
		List<Entity> entities = pick(d.get("entityUids"), entityMap);
		List<GulooStampCate> cates = pick(d.get("cateUids"), cateMap);

		GulooStampBuilder1 bpu = BpuFacade.getInstance().getBuilder(MbrBpuType.GulooStamp$Create1);
		if (bpu == null) {
			HmsNotification.error();
			return;
		}
		bpu.appendStampDate(stampDate);
		bpu.appendDesp(str(d.get("desp")));
		bpu.appendRemark(str(d.get("remark")));
		bpu.appendEntityList(entities);
		bpu.appendCateList(cates);

		StringBuilder msg = new StringBuilder();
		if (!bpu.verify(msg)) {
			HmsMessageBox.exclamation(msg.toString());
			return;
		}

		GulooStamp gs = bpu.build(new StringBuilder(), null);
		if (gs == null) {
			HmsNotification.error();
			return;
		}

		HmsNotification.info("新增咕溜[" + gs.getStampDateStr() + "][" + gs.getDesp() + "]成功。");
		refresh(gs.getUid(), true, false);
	}

	@Listen("onDeleteStamp = #divGuloo")
	public void deleteStamp(Event _evt) {
		String uid = str(((Map<?, ?>) _evt.getData()).get("uid"));
		GulooStamp gs = mbrService.loadGulooStampList().stream().filter(s -> s.getUid().equals(uid)).findFirst()
				.orElse(null);
		if (gs == null) {
			HmsNotification.error();
			return;
		}
		HmsMessageBox.confirm("確認刪除咕溜[" + gs.getDesp() + "]?", () -> {
			if (mbrService.deleteGulooStamp(uid)) {
				HmsNotification.info("刪除咕溜[" + gs.getDesp() + "]成功。");
				refresh(null, false, true);
			} else {
				HmsNotification.error();
			}
		});
	}

	/** 標籤顏色：#rrggbb，或空字串表示不指定(灰色) */
	@Listen("onCateColor = #divGuloo")
	public void updateCateColor(Event _evt) {
		Map<?, ?> d = (Map<?, ?>) _evt.getData();
		String uid = str(d.get("uid"));
		String color = str(d.get("color")).toLowerCase();
		if (!color.isEmpty() && !color.matches("#[0-9a-f]{6}")) {
			HmsMessageBox.exclamation("顏色格式不正確。");
			return;
		}
		if (mbrService.updateGulooStampCateColor(uid, color))
			refresh(null, false, false);
		else
			HmsNotification.error();
	}

	// -------------------------------------------------------------------------------
	// ------------------------------成員(Entity)新增/刪除------------------------------
	@Listen("onAddEntity = #divGuloo")
	public void addEntity(Event _evt) {
		Map<?, ?> d = (Map<?, ?>) _evt.getData();
		String alias = str(d.get("alias"));
		if (!checkName(alias, "成員", mbrService.loadEntityList().stream().map(Entity::getAlias)))
			return;

		EntityType type;
		try {
			type = EntityType.get(Integer.parseInt(str(d.get("typeIdx"))));
		} catch (NumberFormatException e) {
			type = EntityType.UNDEFINED;
		}
		if (type == EntityType.UNDEFINED) {
			HmsMessageBox.exclamation("請選擇類型。");
			return;
		}

		long birthDate = 0;
		String birth = str(d.get("birth"));
		if (!birth.isEmpty()) {
			try {
				birthDate = LocalDate.parse(birth).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
			} catch (DateTimeParseException e) {
				HmsMessageBox.exclamation("生日格式不正確。");
				return;
			}
		}

		if (mbrService.createEntity(alias, type, birthDate) == null) {
			HmsNotification.error();
			return;
		}
		HmsNotification.info("新增成員[" + alias + "]成功。");
		refreshAndCloseForm();
	}

	@Listen("onDeleteEntity = #divGuloo")
	public void deleteEntity(Event _evt) {
		String uid = str(((Map<?, ?>) _evt.getData()).get("uid"));
		Entity ett = mbrService.loadEntityList().stream().filter(e -> e.getUid().equals(uid)).findFirst().orElse(null);
		if (ett == null) {
			HmsNotification.error();
			return;
		}
		long used = mbrService.loadGulooStampList().stream()
				.filter(s -> s.getEntityList().stream().anyMatch(e -> e != null && uid.equals(e.getUid()))).count();
		String msg = "確認刪除成員[" + ett.getAlias() + "]?"
				+ (used > 0 ? System.lineSeparator() + "有 " + used + " 個咕溜標記了這位成員，刪除後這些咕溜上將不再顯示。" : "");
		HmsMessageBox.confirm(msg, () -> {
			if (mbrService.deleteEntity(uid)) {
				HmsNotification.info("刪除成員[" + ett.getAlias() + "]成功。");
				refresh(null, false, false);
			} else {
				HmsNotification.error();
			}
		});
	}

	// -------------------------------------------------------------------------------
	// ------------------------------標籤(Cate)新增/刪除-------------------------------
	@Listen("onAddCate = #divGuloo")
	public void addCate(Event _evt) {
		Map<?, ?> d = (Map<?, ?>) _evt.getData();
		String name = str(d.get("name"));
		String color = str(d.get("color")).toLowerCase();
		if (!checkName(name, "標籤", mbrService.loadGulooStampCateList().stream().map(GulooStampCate::getName)))
			return;
		if (!color.isEmpty() && !color.matches("#[0-9a-f]{6}")) {
			HmsMessageBox.exclamation("顏色格式不正確。");
			return;
		}

		if (mbrService.createGulooStampCate(name, color) == null) {
			HmsNotification.error();
			return;
		}
		HmsNotification.info("新增標籤[" + name + "]成功。");
		refreshAndCloseForm();
	}

	@Listen("onDeleteCate = #divGuloo")
	public void deleteCate(Event _evt) {
		String uid = str(((Map<?, ?>) _evt.getData()).get("uid"));
		GulooStampCate gsc = mbrService.loadGulooStampCateList().stream().filter(c -> c.getUid().equals(uid))
				.findFirst().orElse(null);
		if (gsc == null) {
			HmsNotification.error();
			return;
		}
		long used = mbrService.loadGulooStampList().stream()
				.filter(s -> s.getCateList().stream().anyMatch(c -> c != null && uid.equals(c.getUid()))).count();
		String msg = "確認刪除標籤[" + gsc.getName() + "]?"
				+ (used > 0 ? System.lineSeparator() + "有 " + used + " 個咕溜使用這個標籤，刪除後這些咕溜上將不再顯示。" : "");
		HmsMessageBox.confirm(msg, () -> {
			if (mbrService.deleteGulooStampCate(uid)) {
				HmsNotification.info("刪除標籤[" + gsc.getName() + "]成功。");
				refresh(null, false, false);
			} else {
				HmsNotification.error();
			}
		});
	}

	/** 快速輸入以 @名稱、#名稱 比對，所以名稱必填、不可含空白、不可重複(不分大小寫) */
	private static boolean checkName(String _name, String _label, java.util.stream.Stream<String> _existing) {
		String msg = null;
		if (_name.isEmpty())
			msg = "請輸入" + _label + "名稱。";
		else if (_name.matches(".*\\s.*"))
			msg = _label + "名稱不可包含空白。";
		else if (_existing.anyMatch(n -> _name.equalsIgnoreCase(n)))
			msg = "已經有名為[" + _name + "]的" + _label + "。";
		if (msg == null)
			return true;
		HmsMessageBox.exclamation(msg);
		return false;
	}

	// -------------------------------------------------------------------------------
	/** 重新載入全部資料並交給前端繪製 */
	@SuppressWarnings("unchecked")
	private void refresh(String _newUid, boolean _clearQuick, boolean _closeDrawer) {
		JSONObject opts = new JSONObject();
		opts.put("newUid", _newUid);
		opts.put("clearQuick", _clearQuick);
		opts.put("closeDrawer", _closeDrawer);
		refresh(opts);
	}

	/** 新增成員／標籤成功後：重新載入並收起側欄表單 */
	@SuppressWarnings("unchecked")
	private void refreshAndCloseForm() {
		JSONObject opts = new JSONObject();
		opts.put("closeForm", true);
		refresh(opts);
	}

	private void refresh(JSONObject opts) {
		String args = "'" + root.getUuid() + "'," + toJson() + "," + opts.toJSONString();
		// 等 gulooTimeline.js 載入後才繪製
		Clients.evalJavaScript(
				"(function f(){if(window.hmsGuloo)hmsGuloo.render(" + args + ");else setTimeout(f,50);})()");
	}

	@SuppressWarnings("unchecked")
	private String toJson() {
		JSONArray entities = new JSONArray();
		for (Entity e : mbrService.loadEntityList()) {
			JSONObject o = new JSONObject();
			o.put("uid", e.getUid());
			o.put("alias", e.getAlias());
			o.put("typeIdx", e.getTypeIdx());
			o.put("birth", toDateStr(e.getBirthDate()));
			entities.add(o);
		}

		JSONArray cates = new JSONArray();
		for (GulooStampCate c : mbrService.loadGulooStampCateList()) {
			JSONObject o = new JSONObject();
			o.put("uid", c.getUid());
			o.put("name", c.getName());
			o.put("color", c.getColor());
			cates.add(o);
		}

		JSONArray stamps = new JSONArray();
		for (GulooStamp s : mbrService.loadGulooStampList()) {
			JSONObject o = new JSONObject();
			o.put("uid", s.getUid());
			o.put("date", toDateStr(s.getStampDate()));
			o.put("desp", s.getDesp());
			o.put("remark", s.getRemark());
			JSONArray eu = new JSONArray();
			s.getEntityList().stream().filter(Objects::nonNull).forEach(e -> eu.add(e.getUid()));
			o.put("entities", eu);
			JSONArray cu = new JSONArray();
			s.getCateList().stream().filter(Objects::nonNull).forEach(c -> cu.add(c.getUid()));
			o.put("cates", cu);
			stamps.add(o);
		}

		JSONObject json = new JSONObject();
		json.put("entities", entities);
		json.put("cates", cates);
		json.put("stamps", stamps);
		log.debug("guloo stamps: {}", stamps.size());
		return json.toJSONString();
	}

	// -------------------------------------------------------------------------------
	private static String toDateStr(long _millis) {
		return _millis <= 0 ? "" : Instant.ofEpochMilli(_millis).atZone(ZoneId.systemDefault()).toLocalDate().toString();
	}

	private static String str(Object _o) {
		return _o == null ? "" : String.valueOf(_o).trim();
	}

	private static <T> Map<String, T> toMap(List<T> _list, Function<T, String> _key) {
		return _list.stream().collect(Collectors.toMap(_key, t -> t, (a, b) -> a));
	}

	private static <T> List<T> pick(Object _uids, Map<String, T> _map) {
		List<T> list = new ArrayList<>();
		if (_uids instanceof Collection)
			for (Object u : (Collection<?>) _uids) {
				T t = _map.get(String.valueOf(u));
				if (t != null && !list.contains(t))
					list.add(t);
			}
		return list;
	}
}
