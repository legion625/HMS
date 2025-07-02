package hms_kernel.membership.dto;

public class GulooStampCreateObj {
	private long stampDate; // 日期
	private String desp; // 簡述
	private String remark; // 備註

	public long getStampDate() {
		return stampDate;
	}

	public void setStampDate(long stampDate) {
		this.stampDate = stampDate;
	}

	public String getDesp() {
		return desp;
	}

	public void setDesp(String desp) {
		this.desp = desp;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

}
