package com.gizlocorp.gnvoice.xml.message;

import java.util.List;

public class CeDisResponse {
	
	private String order_number;
	private String cedis_virtual;
	private String vendor_id;
	private String location;
	private String location_type;
	private String vendor_type;
	private String order_status;
	private List<ValidItem> listItem;
	
	
	public String getCedis_virtual() {
		return cedis_virtual;
	}
	public void setCedis_virtual(String cedis_virtual) {
		this.cedis_virtual = cedis_virtual;
	}
	public String getVendor_id() {
		return vendor_id;
	}
	public void setVendor_id(String vendor_id) {
		this.vendor_id = vendor_id;
	}
	public String getLocation_type() {
		return location_type;
	}
	public void setLocation_type(String location_type) {
		this.location_type = location_type;
	}
	public String getVendor_type() {
		return vendor_type;
	}
	public void setVendor_type(String vendor_type) {
		this.vendor_type = vendor_type;
	}
	public String getOrder_number() {
		return order_number;
	}
	public void setOrder_number(String order_number) {
		this.order_number = order_number;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getOrder_status() {
		return order_status;
	}
	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}
	public List<ValidItem> getListItem() {
		return listItem;
	}
	public void setListItem(List<ValidItem> listItem) {
		this.listItem = listItem;
	}
	@Override
	public String toString() {
		return "CeDisResponse [order_number=" + order_number
				+ ", cedis_virtual=" + cedis_virtual + ", vendor_id="
				+ vendor_id + ", location=" + location + ", location_type="
				+ location_type + ", vendor_type=" + vendor_type
				+ ", order_status=" + order_status + ", listItem=" + listItem
				+ "]";
	}
	
	
	
	

}
