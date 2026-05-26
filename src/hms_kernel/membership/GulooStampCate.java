package hms_kernel.membership;

import hms_kernel.HmsObjectModel;
import hms_kernel.data.membership.MembershipDataService;
import legion.DataServiceFactory;

public class GulooStampCate extends HmsObjectModel {

	// -------------------------------------------------------------------------------
	// ----------------------------------attributes-----------------------------------
	private String name;
	private String color; // 章的主色調，例如紅色代表熱情、藍色代表回憶，可以作為標籤底色

	// -------------------------------------------------------------------------------
	// ----------------------------------constructor----------------------------------
	private GulooStampCate() {
	}

	static GulooStampCate newInstance() {
		GulooStampCate cate = new GulooStampCate();
		cate.configNewInstance();
		return cate;
	}

	public static GulooStampCate getInstance(String _uid, long _objectCreateTime, long _objectUpdateTime) {
		GulooStampCate cate = new GulooStampCate();
		cate.configGetInstance(_uid, _objectCreateTime, _objectUpdateTime);
		return cate;
	}

	// -------------------------------------------------------------------------------
	// ---------------------------------getter&setter---------------------------------
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	// -------------------------------------------------------------------------------
	// ----------------------------------ObjectModel----------------------------------
	@Override
	protected boolean save() {
		return DataServiceFactory.getInstance().getService(MembershipDataService.class).saveGulooStampCate(this);
	}

	@Override
	protected boolean delete() {
		return DataServiceFactory.getInstance().getService(MembershipDataService.class)
				.deleteGulooStampCate(this.getUid());
	}

	// -------------------------------------------------------------------------------
	// --------------------------------GulooStampCate---------------------------------
	public static GulooStampCate create(String _name, String _color) {
		GulooStampCate cate = newInstance();
		cate.setName(_name);
		cate.setColor(_color);
		return cate.save() ? cate : null;
	}

}
