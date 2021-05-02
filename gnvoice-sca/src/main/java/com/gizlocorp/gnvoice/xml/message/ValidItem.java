package com.gizlocorp.gnvoice.xml.message;


public class ValidItem {
	
    private String item;
    private String itemType;
    
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	@Override
	public String toString() {
		return "ValidItem [item=" + item + ", itemType=" + itemType + "]";
	}
	
	
    
    

}
