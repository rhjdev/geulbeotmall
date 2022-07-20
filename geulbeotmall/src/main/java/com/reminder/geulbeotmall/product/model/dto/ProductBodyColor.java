package com.reminder.geulbeotmall.product.model.dto;

public enum ProductBodyColor {
	//A
	APPLEMINT("applemint", "애플민트", "color: #83d9d0;"),
	AQUAFRESH("aquafresh", "아쿠아프레시", "color: #25bdae;"),
	//B
	BLACK("black", "블랙", "color: #000000;"),
	BLUEPEARL("bluepearl", "블루펄", "background: linear-gradient(90deg, rgba(54,137,160,1) 0%, rgba(127,190,207,1) 50%, rgba(54,137,160,1) 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;"),
	//C
	CANARY("canary", "카나리", "color: #fbd506;"),
	CANDYPINK("candypink", "캔디핑크", "color: #ef4d98;"),
	CITRUS("citrus", "시트러스", "color: #f8d04c;"),
	CHOCOLATEMILK("chocolatemilk", "초콜릿밀크", "color: #b68660;"),
	//D
	DANDELION("dandelion", "단델리온", "background: linear-gradient(to right, #ffc901 0%, #0ccc91 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;"),
	//E
	//F
	//G
	GOLD("gold", "골드", "color: #e2cc9d;"),
	GRAY("gray", "그레이", "color: #99abc3;"),
	GREENPEARL("greenpearl", "그린펄", "background: linear-gradient(90deg, rgba(158,190,93,1) 0%, rgba(210,227,173,1) 50%, rgba(158,190,93,1) 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;"),
	//H
	//I
	INDIGO("indigo", "인디고", "color: #1a3d75;"),
	//J
	//K
	//L
	LAVENDER("lavender", "라벤더", "color: #d6c7e6;"),
	LILY("lily", "릴리", "color: #d9d9d9;"),
	//M
	MIDNIGHT("midnight", "미드나이트", "color: #003d7c;"),
	MINTPEARL("mintpearl", "민트펄", "background: linear-gradient(90deg, rgba(81,174,198,1) 0%, rgba(164,220,233,1) 50%, rgba(81,174,198,1) 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;"),
	MUSTARDYELLOW("mustardyellow", "머스타드옐로우", "color: #efab00;"),
	//N
	NEONLIME("neonlime", "네온라임", "color: #c8dc5f;"),
	NEONORAGE("neonorange", "네온오렌지", "color: #f06141;"),
	//O
	OATMEAL("oatmeal", "오트밀", "color: #cdbe9f;"),
	ORANGEPEARL("orangepearl", "오렌지펄", "background: linear-gradient(90deg, rgba(247,151,73,1) 0%, rgba(253,219,192,1) 50%, rgba(247,151,73,1) 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;"),
	//P
	PAPAYA("papaya", "파파야", "color: #fac1a4;"),
	PEONY("peony", "피오니", "color:#e6b8b8;"),
	PINKPEARL("pinkpearl", "핑크펄", "background: linear-gradient(90deg, rgba(219,153,152,1) 0%, rgba(239,191,191,1) 50%, rgba(219,153,152,1) 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;"),
	PUMPKINORANGE("pumpkinorange", "펌킨오렌지", "color: #ff7900;"),
	//Q
	//R
	ROSEBAY("rosebay", "로즈베이", "background: linear-gradient(to right, #e4bbb7 0%, #8fb5e4 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;"),
	//S
	SAGEGREEN("sagegreen", "세이지그린", "color: #2a918e;"),
	SILVER("silver", "실버", "background: linear-gradient(90deg, rgba(182,183,187,1) 0%, rgba(229,230,232,1) 50%, rgba(182,183,187,1) 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;"),
	SMOKY("smoky", "스모키", "color: #77787c;"),
	STRAWBERRYMILK("strawberrymilk", "스트로베리밀크", "color: #f39bc3;"),
	SODASKY("sodasky", "소다스카이", "color: #65cfe9;"),
	//T
	TANGERINE("tangerine", "탄제린", "color: #f1592a;"),
	//U
	//V
	VIOLA("viola", "비올라", "color: #95b3d7;"),
	VIOLETPEARL("violetpearl", "바이올렛펄", "background: linear-gradient(90deg, rgba(134,106,166,1) 0%, rgba(202,181,226,1) 50%, rgba(134,106,166,1) 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent;"),
	VINTAGEGREEN("vintagegreen", "빈티지그린", "color: #517a6a;"),
	VINTAGEIVORY("vintageivory", "빈티지아이보리", "color: #e2cc9d;"),
	VINTAGEPINK("vintagepink", "빈티지핑크", "color: #af6677;"),
	//W
	WITCHPURPLE("witchpurple", "위치퍼플", "color: #7c109a;"),
	WHITE("white", "화이트", "color: #ffffff;");
	//X
	//Y
	//Z
	
	private final String value;
	private final String label;
	private final String color;
	
	ProductBodyColor(String value, String label, String color) {
		this.value = value;
		this.label = label;
		this.color = color;
	}
	
	public String getValue() {
		return value;
	}
	
	public String getLabel() {
		return label;
	}
	
	public String getColor() {
		return color;
	}
}
