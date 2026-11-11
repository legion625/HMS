package hms.web.control.zk.membership;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import hms_kernel.membership.GulooStampCateConj;
import hms_kernel.membership.GulooStampEntityConj;
import hms_kernel.membership.MembershipService;
import hms_kernel.membership.type.EntityType;
import legion.BusinessServiceFactory;
import legion.biz.BpuFacade;
import legion.util.DateFormatUtil;

/**
 * 咕溜時光軸(電腦版 prototype)。
 * 畫面、篩選與快速輸入解析都在 gulooTimeline.js；這裡負責提供資料，以及處理咕溜、成員、標籤的新增/刪除。
 *
 * 資料由 reload() 整批載入(固定 5 次查詢，不隨咕溜筆數增加)，存成 composer 上的 model；
 * 事件處理一律以 uid 從 model 取用，不再為了找物件重新查詢。
 */
public class GulooTimelineComposer extends SelectorComposer<Component> {
	private Logger log = LoggerFactory.getLogger(getClass());

	private MembershipService mbrService = BusinessServiceFactory.getInstance().getService(MembershipService.class);

	private Component root;

	// -------------------------------------------------------------------------------
	// -------------------------------------model-------------------------------------
	private final Map<String, Entity> entityMap = new LinkedHashMap<>();
	private final Map<String, GulooStampCate> cateMap = new LinkedHashMap<>();
	private final Map<String, GulooStamp> stampMap = new LinkedHashMap<>();
	/** stampUid -> 成員 uid(只含存在的成員) */
	private final Map<String, List<String>> stampEntityUids = new HashMap<>();
	/** stampUid -> 標籤 uid(只含存在的標籤) */
	private final Map<String, List<String>> stampCateUids = new HashMap<>();

	/** 整批重新載入 model：成員、標籤、咕溜、兩種關聯各一次查詢 */
	private void reload() {
		long t = System.currentTimeMillis();
		entityMap.clear();
		cateMap.clear();
		stampMap.clear();
		stampEntityUids.clear();
		stampCateUids.clear();

		mbrService.loadEntityList().forEach(e -> entityMap.put(e.getUid(), e));
		mbrService.loadGulooStampCateList().forEach(c -> cateMap.put(c.getUid(), c));
		mbrService.loadGulooStampList().forEach(s -> stampMap.put(s.getUid(), s));

		// 關聯表可能留有已刪除的咕溜/成員/標籤(刪除時不會連帶刪關聯)，對不上的略過
		for (GulooStampEntityConj c : mbrService.loadAllGulooStampEntityConjList())
			if (stampMap.containsKey(c.getStampUid()) && entityMap.containsKey(c.getEntityUid()))
				stampEntityUids.computeIfAbsent(c.getStampUid(), k -> new ArrayList<>()).add(c.getEntityUid());
		for (GulooStampCateConj c : mbrService.loadAllGulooStampCateConjList())
			if (stampMap.containsKey(c.getStampUid()) && cateMap.containsKey(c.getCateUid()))
				stampCateUids.computeIfAbsent(c.getStampUid(), k -> new ArrayList<>()).add(c.getCateUid());

		log.debug("reload guloo model: {} stamps, {} ms", stampMap.size(), System.currentTimeMillis() - t);
	}

	private List<String> entityUidsOf(String _stampUid) {
		return stampEntityUids.getOrDefault(_stampUid, Collections.emptyList());
	}

	private List<String> cateUidsOf(String _stampUid) {
		return stampCateUids.getOrDefault(_stampUid, Collections.emptyList());
	}

	/** 有幾個咕溜使用這個成員/標籤 */
	private static long countUsage(Map<String, List<String>> _index, String _uid) {
		return _index.values().stream().filter(l -> l.contains(_uid)).count();
	}

	// -------------------------------------------------------------------------------
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		root = comp;
		refresh(null, false, false);
	}

	// -------------------------------------------------------------------------------
	// -------------------------------------咕溜------------------------------------
	@Listen("onQuickAdd = #divGuloo")
	public void quickAdd(Event _evt) {
		Map<?, ?> d = (Map<?, ?>) _evt.getData();

		Date stampDate = DateFormatUtil.parse(str(d.get("date")));
		if (stampDate == null) {
			HmsMessageBox.exclamation("日期格式不正確。");
			return;
		}

		GulooStampBuilder1 bpu = BpuFacade.getInstance().getBuilder(MbrBpuType.GulooStamp$Create1);
		if (bpu == null) {
			HmsNotification.error();
			return;
		}
		bpu.appendStampDate(stampDate.getTime());
		bpu.appendDesp(str(d.get("desp")));
		bpu.appendRemark(str(d.get("remark")));
		bpu.appendEntityList(pick(d.get("entityUids"), entityMap));
		bpu.appendCateList(pick(d.get("cateUids"), cateMap));

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
		String uid = uidOf(_evt);
		GulooStamp gs = stampMap.get(uid);
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

	// -------------------------------------------------------------------------------
	// ------------------------------成員(Entity)新增/刪除------------------------------
	@Listen("onAddEntity = #divGuloo")
	public void addEntity(Event _evt) {
		Map<?, ?> d = (Map<?, ?>) _evt.getData();
		String alias = str(d.get("alias"));
		if (!checkName(alias, "成員", entityMap.values().stream().map(Entity::getAlias).toArray(String[]::new)))
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

		// 生日可不填(0 表示未指定)
		long birthDate = 0;
		String birth = str(d.get("birth"));
		if (!birth.isEmpty()) {
			Date bd = DateFormatUtil.parse(birth);
			if (bd == null) {
				HmsMessageBox.exclamation("生日格式不正確。");
				return;
			}
			birthDate = bd.getTime();
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
		String uid = uidOf(_evt);
		Entity ett = entityMap.get(uid);
		if (ett == null) {
			HmsNotification.error();
			return;
		}
		long used = countUsage(stampEntityUids, uid);
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
	// ------------------------------標籤(Cate)新增/刪除/顏色-------------------------------
	@Listen("onAddCate = #divGuloo")
	public void addCate(Event _evt) {
		Map<?, ?> d = (Map<?, ?>) _evt.getData();
		String name = str(d.get("name"));
		String color = str(d.get("color")).toLowerCase();
		if (!checkName(name, "標籤", cateMap.values().stream().map(GulooStampCate::getName).toArray(String[]::new)))
			return;
		if (!isValidColor(color)) {
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
		String uid = uidOf(_evt);
		GulooStampCate gsc = cateMap.get(uid);
		if (gsc == null) {
			HmsNotification.error();
			return;
		}
		long used = countUsage(stampCateUids, uid);
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

	/** 標籤顏色：#rrggbb，或空字串表示不指定(灰色) */
	@Listen("onCateColor = #divGuloo")
	public void updateCateColor(Event _evt) {
		Map<?, ?> d = (Map<?, ?>) _evt.getData();
		String uid = str(d.get("uid"));
		String color = str(d.get("color")).toLowerCase();
		if (!cateMap.containsKey(uid)) {
			HmsNotification.error();
			return;
		}
		if (!isValidColor(color)) {
			HmsMessageBox.exclamation("顏色格式不正確。");
			return;
		}
		if (mbrService.updateGulooStampCateColor(uid, color))
			refresh(null, false, false);
		else
			HmsNotification.error();
	}

	// -------------------------------------------------------------------------------
	/** 快速輸入以 @名稱、#名稱 比對，所以名稱必填、不可含空白、不可重複(不分大小寫) */
	private static boolean checkName(String _name, String _label, String[] _existing) {
		String msg = null;
		if (_name.isEmpty())
			msg = "請輸入" + _label + "名稱。";
		else if (_name.matches(".*\\s.*"))
			msg = _label + "名稱不可包含空白。";
		else
			for (String n : _existing)
				if (_name.equalsIgnoreCase(n)) {
					msg = "已經有名為[" + _name + "]的" + _label + "。";
					break;
				}
		if (msg == null)
			return true;
		HmsMessageBox.exclamation(msg);
		return false;
	}

	private static boolean isValidColor(String _color) {
		return _color.isEmpty() || _color.matches("#[0-9a-f]{6}");
	}

	// -------------------------------------------------------------------------------
	/** 重新載入 model 並交給前端繪製 */
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
		reload();
		String args = "'" + root.getUuid() + "'," + toJson() + "," + opts.toJSONString();
		// 等 gulooTimeline.js 載入後才繪製
		Clients.evalJavaScript(
				"(function f(){if(window.hmsGuloo)hmsGuloo.render(" + args + ");else setTimeout(f,50);})()");
	}

	/** 由 model 產生前端資料(不查詢) */
	@SuppressWarnings("unchecked")
	private String toJson() {
		JSONArray entities = new JSONArray();
		for (Entity e : entityMap.values()) {
			JSONObject o = new JSONObject();
			o.put("uid", e.getUid());
			o.put("alias", e.getAlias());
			o.put("typeIdx", e.getTypeIdx());
			o.put("birth", toDateStr(e.getBirthDate()));
			entities.add(o);
		}

		JSONArray cates = new JSONArray();
		for (GulooStampCate c : cateMap.values()) {
			JSONObject o = new JSONObject();
			o.put("uid", c.getUid());
			o.put("name", c.getName());
			o.put("color", c.getColor());
			cates.add(o);
		}

		JSONArray stamps = new JSONArray();
		for (GulooStamp s : stampMap.values()) {
			JSONObject o = new JSONObject();
			o.put("uid", s.getUid());
			o.put("date", toDateStr(s.getStampDate()));
			o.put("desp", s.getDesp());
			o.put("remark", s.getRemark());
			JSONArray eu = new JSONArray();
			eu.addAll(entityUidsOf(s.getUid()));
			o.put("entities", eu);
			JSONArray cu = new JSONArray();
			cu.addAll(cateUidsOf(s.getUid()));
			o.put("cates", cu);
			stamps.add(o);
		}

		JSONObject json = new JSONObject();
		json.put("entities", entities);
		json.put("cates", cates);
		json.put("stamps", stamps);
		return json.toJSONString();
	}

	// -------------------------------------------------------------------------------
	/** yyyy-MM-dd；0 表示未指定，回傳空字串 */
	private static String toDateStr(long _millis) {
		return _millis <= 0 ? "" : DateFormatUtil.transToDate(new Date(_millis));
	}

	private static String str(Object _o) {
		return _o == null ? "" : String.valueOf(_o).trim();
	}

	private static String uidOf(Event _evt) {
		return str(((Map<?, ?>) _evt.getData()).get("uid"));
	}

	/** 前端傳來的 uid 清單換成 model 中的物件(不存在的略過、去重) */
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
