package com.vendomatica.vendroid.Model;

/**
 * Created by shevchenko on 2015-12-27.
 */
public class MenuItemButton {
    public String txtMenu;
    public int imgDrawable;
    public int type;

    public MenuItemButton(){
        txtMenu = "";
        imgDrawable = 0;
        type = 0;
    }
    public MenuItemButton(String strMenu, int drawable){
        this.txtMenu = strMenu;
        this.imgDrawable = drawable;
        this.type = 0;
    }
    public MenuItemButton(String strMenu, int drawable, int iType){
        this.txtMenu = strMenu;
        this.imgDrawable = drawable;
        this.type = iType;
    }

}
