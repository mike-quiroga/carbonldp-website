package com.base22.carbon.models;

public class Platform {
	private String title = "";
	private String footer = "";
	private Menu menu = null;
	
	public class Menu {
		private String main = "";
		private String secondary = "";
		
		public String getMain() {
			return main;
		}
		public void setMain(String main) {
			this.main = main;
		}
		public String getSecondary() {
			return secondary;
		}
		public void setSecondary(String secondary) {
			this.secondary = secondary;
		}
	}
	
	public Platform(){
		this.setMenu(new Menu());
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFooter() {
		return footer;
	}

	public void setFooter(String footer) {
		this.footer = footer;
	}

	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}
}
