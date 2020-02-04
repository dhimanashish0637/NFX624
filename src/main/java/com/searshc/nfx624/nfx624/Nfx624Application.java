package com.searshc.nfx624.nfx624;

import java.io.IOException;

public class Nfx624Application {

	public static void main(String[] args) {
		Imple imple = new Imple();
		try {
			imple.getAllJson();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
