package com.searshc.nfx624.nfx624;

public class FX624 {
	protected String STORE;
	protected String DIV;
	protected String PRICE;
	protected String salescheck_STR;
	protected String salescheck_REG;
	protected String salescheck_TRANS;
	protected String MM;
	protected String SLS1;
	protected String DD;
	protected String SLS2;
	protected String YYYY;

	public FX624() {
		STORE = "00000";
		DIV = "000";
		PRICE = "0000000";
		salescheck_STR = "00000";
		salescheck_REG = "000";
		salescheck_TRANS = "0000";
		MM = "00";
		SLS1 = "/";
		DD = "00";
		SLS2 = "/";
		YYYY = "0000";
	}

	@Override
	public String toString() {
		return STORE + DIV + PRICE + salescheck_STR + salescheck_REG + salescheck_TRANS + MM + SLS1 + DD + SLS2 + YYYY;
	}

	public String getSTORE() {
		return STORE;
	}

	public void setSTORE(String sTORE) {
		STORE = sTORE;
	}

	public String getDIV() {
		return DIV;
	}

	public void setDIV(String dIV) {
		DIV = dIV;
	}

	public String getPRICE() {
		return PRICE;
	}

	public void setPRICE(String pRICE) {
		PRICE = pRICE;
	}

	public String getSalescheck_STR() {
		return salescheck_STR;
	}

	public void setSalescheck_STR(String salescheck_STR) {
		this.salescheck_STR = salescheck_STR;
	}

	public String getSalescheck_REG() {
		return salescheck_REG;
	}

	public void setSalescheck_REG(String salescheck_REG) {
		this.salescheck_REG = salescheck_REG;
	}

	public String getSalescheck_TRANS() {
		return salescheck_TRANS;
	}

	public void setSalescheck_TRANS(String salescheck_TRANS) {
		this.salescheck_TRANS = salescheck_TRANS;
	}

	public String getMM() {
		return MM;
	}

	public void setMM(String mM) {
		MM = mM;
	}

	public String getSLS1() {
		return SLS1;
	}

	public void setSLS1(String sLS1) {
		SLS1 = sLS1;
	}

	public String getDD() {
		return DD;
	}

	public void setDD(String dD) {
		DD = dD;
	}

	public String getSLS2() {
		return SLS2;
	}

	public void setSLS2(String sLS2) {
		SLS2 = sLS2;
	}

	public String getYYYY() {
		return YYYY;
	}

	public void setYYYY(String yYYY) {
		YYYY = yYYY;
	}

}
