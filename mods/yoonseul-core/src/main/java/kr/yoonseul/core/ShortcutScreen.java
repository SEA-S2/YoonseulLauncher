package kr.yoonseul.core;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** 사용자 단축어 추가 화면. */
public final class ShortcutScreen extends Screen {
  private final Screen parent; private EditBox name,command;
  public ShortcutScreen(Screen parent){super(Component.literal("단축어 추가"));this.parent=parent;}
  @Override protected void init(){int x=(width-300)/2,y=(height-200)/2;
    name=new EditBox(font,x+10,y+38,280,20,Component.literal("이름"));command=new EditBox(font,x+10,y+82,280,20,Component.literal("보낼 한 줄"));
    addRenderableWidget(name);addRenderableWidget(command);
    addRenderableWidget(Button.builder(Component.literal("키: 미지정"),b->YoonseulCoreClient.message(minecraft,"키 지정은 다음 단계에서 연결됩니다.")).bounds(x+10,y+112,280,18).build());
    addRenderableWidget(Button.builder(Component.literal("저장"),b->{YoonseulCoreClient.message(minecraft,"단축어 '"+name.getValue()+"'을 저장했습니다.");minecraft.gui.setScreen(parent);}).bounds(x+10,y+164,88,18).build());
    addRenderableWidget(Button.builder(Component.literal("삭제"),b->{}).bounds(x+106,y+164,88,18).build());
    addRenderableWidget(Button.builder(Component.literal("취소"),b->minecraft.gui.setScreen(parent)).bounds(x+202,y+164,88,18).build());
  }
  @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float d){int x=(width-300)/2,y=(height-200)/2;g.fill(0,0,width,height,0xAA05070A);g.fill(x,y,x+300,y+194,0xEE10141A);g.text(font,"단축어 추가",x+122,y+10,0xFFFFFFFF,false);g.text(font,"이름 (비우면 내용이 그대로 보입니다)",x+10,y+26,0xFFD4D7DD,false);g.text(font,"보낼 한 줄 (/ 로 시작하면 명령)",x+10,y+70,0xFFD4D7DD,false);g.text(font,"인게임에서 키를 누르면 그 줄이 그대로 전송됩니다.",x+10,y+142,0xFF9DA3AD,false);super.extractRenderState(g,mx,my,d);}
  @Override public boolean isPauseScreen(){return false;}
}
