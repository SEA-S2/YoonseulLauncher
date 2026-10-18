package kr.yoonseul.core;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class CalculatorScreen extends Screen {
  private EditBox input; private String result="";
  public CalculatorScreen(){super(Component.literal("윤슬 계산기"));}
  @Override protected void init(){int x=(width-430)/2,y=(height-220)/2;input=new EditBox(font,x+22,y+58,386,28,Component.literal("계산식"));addRenderableWidget(input);
    addRenderableWidget(Button.builder(Component.literal("계산"),b->calculate()).bounds(x+22,y+104,185,26).build());
    addRenderableWidget(Button.builder(Component.literal("닫기"),b->onClose()).bounds(x+223,y+104,185,26).build());}
  private void calculate(){try{double v=new Parser(input.getValue()).parse();result=(v==Math.rint(v)?Long.toString((long)v):Double.toString(v));}catch(Exception e){result="계산식을 확인해주세요";}}
  @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float d){int x=(width-430)/2,y=(height-220)/2;g.fill(0,0,width,height,0xAA080B10);g.fill(x,y,x+430,y+190,0xF01A2029);g.text(font,"윤슬 계산기",x+22,y+20,0xFFFFFFFF,false);g.text(font,"사칙연산·괄호·K/M/B 단위를 사용할 수 있어요.",x+22,y+40,0xFFBFC5CE,false);g.text(font,"결과: "+result,x+22,y+148,0xFF9BE7D7,false);super.extractRenderState(g,mx,my,d);} @Override public boolean isPauseScreen(){return false;}
  static final class Parser{final String s;int p;Parser(String s){this.s=s.replace(" ","").toUpperCase();}double parse(){double v=expr();if(p!=s.length())throw new IllegalArgumentException();return v;}double expr(){double v=term();while(p<s.length()&&(s.charAt(p)=='+'||s.charAt(p)=='-')){char c=s.charAt(p++);double n=term();v=c=='+'?v+n:v-n;}return v;}double term(){double v=factor();while(p<s.length()&&(s.charAt(p)=='*'||s.charAt(p)=='/')){char c=s.charAt(p++);double n=factor();v=c=='*'?v*n:v/n;}return v;}double factor(){if(p<s.length()&&s.charAt(p)=='-'){p++;return-factor();}if(p<s.length()&&s.charAt(p)=='('){p++;double v=expr();if(p>=s.length()||s.charAt(p++)!=')')throw new IllegalArgumentException();return unit(v);}int st=p;while(p<s.length()&&(Character.isDigit(s.charAt(p))||s.charAt(p)=='.'))p++;if(st==p)throw new IllegalArgumentException();return unit(Double.parseDouble(s.substring(st,p)));}double unit(double v){if(p<s.length()){char c=s.charAt(p);if(c=='K'||c=='M'||c=='B'){p++;return v*(c=='K'?1e3:c=='M'?1e6:1e9);}}return v;}}
}
