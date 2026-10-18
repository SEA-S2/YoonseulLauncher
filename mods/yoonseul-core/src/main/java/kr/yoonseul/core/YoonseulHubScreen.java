package kr.yoonseul.core;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import java.util.List;

/** 사진의 클라이언트 허브 흐름을 윤슬용으로 새로 구현한 화면. */
public final class YoonseulHubScreen extends Screen {
    record Module(String name,String desc,String cat) {}
    private static final List<Module> MODULES=List.of(
      new Module("계산기","사칙연산·괄호·K/M/B 단위 계산","도구"),new Module("타이머","카운트다운·스톱워치 + 화면 HUD","도구"),
      new Module("귓속말","귓속말을 별도 창으로 모아보기","채팅"),new Module("사용자 지정 채팅창","폭·높이·글자 크기와 타임스탬프","채팅"),
      new Module("프리뷰 카메라","독립 오빗 카메라 포토 모드","카메라"),new Module("줌","키를 누르는 동안 시야 확대 (기본 Z)","카메라"),
      new Module("자동 달리기","토글 키로 자동 전진 달리기","조작"),new Module("밝기 조절","토글 키로 추가 밝기","시각"),
      new Module("사용자 지정 시간","내 화면의 시간대만 변경","시각"),new Module("글꼴 크기","글꼴별 크기 조절","시각"),
      new Module("떨어진 아이템 광선","바닥 아이템 위에 색 광선 표시","시각"),new Module("어둠 효과 제거","워든·스컬크 어둠 효과 제거","시각"),
      new Module("인챈트 만렙 색상","최대 레벨 노랑·그 외 초록","편의"),new Module("남은 체력 표시","엔티티 머리 위에 체력 표시","정보"),
      new Module("낚시 조절","내 입질음만 일정한 크기로","편의"),new Module("FPS 표시","초당 프레임을 HUD에 표시","정보"),
      new Module("실시간 시계","현실 시각을 HUD에 표시","정보"),new Module("인게임 시각","게임 내 시각과 낮/밤 표시","정보"),
      new Module("핑 표시","서버 응답 시간(ms) 표시","정보"),new Module("시스템 표시","메모리 사용량과 CPU 부하 표시","정보"),
      new Module("물약 효과","효과와 남은 시간을 표시","정보"),new Module("장비 내구도","방어구 아이콘과 내구도 표시","정보"),
      new Module("도구 내구도","양손 도구 아이콘과 내구도 표시","정보"),new Module("인벤토리 HUD","인벤토리 27칸과 남은 슬롯 표시","정보"),
      new Module("이동 속도","수평 이동 속도를 HUD에 표시","정보"),new Module("아이템 개수","든 아이템의 인벤토리 전체 합계","정보"),
      new Module("키입력 표시","WASD·점프·달리기·마우스 CPS","정보"));
    private static final String[] CATS={"전체","도구","채팅","카메라","조작","시각","편의","정보"};
    private String category="전체", left="모드"; private int page=0; private final String section;
    public YoonseulHubScreen(){this("모드");} public YoonseulHubScreen(String section){super(Component.literal("윤슬 클라이언트 허브"));this.section=section;}
    private Button b(String s,int x,int y,int w,int h,Runnable r){return Button.builder(Component.literal(s),v->r.run()).bounds(x,y,w,h).build();}
    private List<Module> filtered(){return MODULES.stream().filter(m->category.equals("전체")||m.cat.equals(category)).toList();}
    private int bx(){return (width-Math.min(840,width-8))/2;} private int by(){return Math.max(4,(height-Math.min(530,height-8))/2);}
    @Override protected void init(){
      int x=bx(),y=by(),w=Math.min(840,width-8),h=Math.min(530,height-8);
      String[] lefts={"소셜","모드","단축어","스킨","치장","설정"};
      for(int i=0;i<lefts.length;i++){String n=lefts[i];addRenderableWidget(b(n,x+14,y+64+i*48,176,32,()->{left=n;page=0;rebuildWidgets();}));}
      if(left.equals("모드")){
        int cw=(w-226)/8; for(int i=0;i<CATS.length;i++){String c=CATS[i];addRenderableWidget(b(c,x+220+i*cw,y+18,cw-4,28,()->{category=c;page=0;rebuildWidgets();}));}
        List<Module> ms=filtered(); int from=Math.min(page*8,Math.max(0,ms.size()-1)),to=Math.min(from+8,ms.size());
        for(int i=from;i<to;i++){Module m=ms.get(i);int p=i-from,col=p%2,row=p/2,cx=x+220+col*((w-238)/2+12),cy=y+70+row*102;
          addRenderableWidget(b("설정 ›",cx+(w-238)/2-78,cy+65,66,20,()->openModule(m)));
        }
        if(from>0)addRenderableWidget(b("‹ 이전",x+w-180,y+h-32,72,20,()->{page--;rebuildWidgets();}));
        if(to<ms.size())addRenderableWidget(b("다음 ›",x+w-96,y+h-32,72,20,()->{page++;rebuildWidgets();}));
      }else if(left.equals("단축어")){addRenderableWidget(b("+ 단축어 추가",x+230,y+76,180,28,()->minecraft.gui.setScreen(new ShortcutScreen(this))));}
      else if(left.equals("설정")){addRenderableWidget(b("HUD 배치 변경",x+230,y+76,w-260,38,()->YoonseulCoreClient.message(minecraft,"HUD 배치 화면을 구현 중입니다.")));addRenderableWidget(b("설정 초기화",x+230,y+132,w-260,38,()->{YoonseulCoreClient.config=new YoonseulConfig();YoonseulCoreClient.config.save();rebuildWidgets();}));}
      addRenderableWidget(b("닫기",x+20,y+h-32,80,20,this::onClose));
    }
    private void openModule(Module m){
      YoonseulConfig c=YoonseulCoreClient.config;
      switch(m.name){
        case "계산기" -> minecraft.gui.setScreen(new CalculatorScreen());
        case "타이머" -> minecraft.gui.setScreen(new TimerScreen(this));
        case "밝기 조절" -> {c.brightness=!c.brightness;YoonseulCoreClient.changed(minecraft,m.name,c.brightness);}
        case "자동 달리기" -> {c.autoSprint=!c.autoSprint;YoonseulCoreClient.changed(minecraft,m.name,c.autoSprint);}
        case "인벤토리 HUD" -> {c.inventoryHud=!c.inventoryHud;YoonseulCoreClient.changed(minecraft,m.name,c.inventoryHud);}
        case "사용자 지정 시간" -> {c.customTime=!c.customTime;YoonseulCoreClient.changed(minecraft,m.name,c.customTime);}
        default -> YoonseulCoreClient.message(minecraft,m.name+" 세부 설정을 연결 중입니다.");
      }
    }
    @Override public void extractRenderState(GuiGraphicsExtractor g,int mx,int my,float d){
      int x=bx(),y=by(),w=Math.min(840,width-8),h=Math.min(530,height-8);g.fill(0,0,width,height,0xAA080B10);g.fill(x,y,x+w,y+h,0xF5101318);g.fill(x+204,y+8,x+206,y+h-8,0xFF30343B);
      g.text(font,"윤슬 클라이언트 허브",x+20,y+22,0xFFF4F6FA,false);g.text(font,left,x+230,y+24,0xFFFFFFFF,false);
      if(left.equals("모드")){List<Module> ms=filtered();int from=Math.min(page*8,Math.max(0,ms.size()-1)),to=Math.min(from+8,ms.size());for(int i=from;i<to;i++){Module m=ms.get(i);int p=i-from,col=p%2,row=p/2,cx=x+220+col*((w-238)/2+12),cy=y+70+row*102,cw=(w-238)/2;g.fill(cx,cy,cx+cw,cy+90,0xFF383B41);g.fill(cx,cy,cx+4,cy+90,0xFFF0F3F8);g.text(font,m.name,cx+18,cy+14,0xFFFFFFFF,false);g.text(font,m.desc,cx+18,cy+38,0xFFCDD0D6,false);}}
      else if(left.equals("단축어")){g.text(font,"자주 쓰는 명령과 문장을 원하는 키에 지정합니다.",x+230,y+54,0xFFB9BDC5,false);}
      else if(left.equals("설정")){g.text(font,"모듈별 설정은 각 모드 카드에서 조절하세요.",x+230,y+54,0xFFB9BDC5,false);}
      else g.text(font,"이 메뉴는 윤슬 서버 기능에 맞춰 추가될 예정입니다.",x+230,y+70,0xFFB9BDC5,false);
      g.text(font,"RShift 닫기",x+20,y+h-54,0xFF8F949D,false);super.extractRenderState(g,mx,my,d);
    }
    @Override public boolean isPauseScreen(){return false;}
}
