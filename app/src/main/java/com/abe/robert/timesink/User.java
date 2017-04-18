package com.abe.robert.timesink;

/**
 * Used for reading/writing objects to the firebase database. stores whether the checkbox values
 * should begin checked or unchecked.
 *
 * Created by Abe on 4/11/2017.
 */

public class User {

    private boolean checkbox1, checkbox2, checkbox3, checkbox4, checkbox5, checkbox6, checkbox7, checkbox8, checkbox9, checkbox10;
    private String custom;
    private int minutes;

    public User(boolean checkbox1, boolean checkbox2, boolean checkbox3, boolean checkbox4,
                boolean checkbox5, boolean checkbox6, boolean checkbox7, boolean checkbox8,
                boolean checkbox9, boolean checkbox10, String custom, int minutes) {
        this.checkbox1 = checkbox1;
        this.checkbox2 = checkbox2;
        this.checkbox3 = checkbox3;
        this.checkbox4 = checkbox4;
        this.checkbox5 = checkbox5;
        this.checkbox6 = checkbox6;
        this.checkbox3 = checkbox7;
        this.checkbox4 = checkbox8;
        this.checkbox5 = checkbox9;
        this.checkbox6 = checkbox10;
        this.custom = custom;
        this.minutes = minutes;
    }

    public boolean isCheckbox7() {
        return checkbox7;
    }

    public void setCheckbox7(boolean checkbox7) {
        this.checkbox7 = checkbox7;
    }

    public boolean isCheckbox8() {
        return checkbox8;
    }

    public void setCheckbox8(boolean checkbox8) {
        this.checkbox8 = checkbox8;
    }

    public boolean isCheckbox9() {
        return checkbox9;
    }

    public void setCheckbox9(boolean checkbox9) {
        this.checkbox9 = checkbox9;
    }

    public boolean isCheckbox10() {
        return checkbox10;
    }

    public void setCheckbox10(boolean checkbox10) {
        this.checkbox10 = checkbox10;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public boolean isCheckbox1() {
        return checkbox1;
    }

    public void setCheckbox1(boolean checkbox1) {
        this.checkbox1 = checkbox1;
    }

    public boolean isCheckbox2() {
        return checkbox2;
    }

    public void setCheckbox2(boolean checkbox2) {
        this.checkbox2 = checkbox2;
    }

    public boolean isCheckbox3() {
        return checkbox3;
    }

    public void setCheckbox3(boolean checkbox3) {
        this.checkbox3 = checkbox3;
    }

    public boolean isCheckbox4() {
        return checkbox4;
    }

    public void setCheckbox4(boolean checkbox4) {
        this.checkbox4 = checkbox4;
    }

    public boolean isCheckbox5() {
        return checkbox5;
    }

    public void setCheckbox5(boolean checkbox5) {
        this.checkbox5 = checkbox5;
    }

    public boolean isCheckbox6() {
        return checkbox6;
    }

    public void setCheckbox6(boolean checkbox6) {
        this.checkbox6 = checkbox6;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}
