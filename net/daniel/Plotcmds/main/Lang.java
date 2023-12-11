package net.daniel.Plotcmds.main;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import com.google.common.base.CaseFormat;

import net.daniel.plotcmd.Utils.MCUtils;

public enum Lang {
	// Class 명이 파일명과 같을 필요는 없음
	// 이름이 파일에 있는 메세지와 같을것, 띄어쓰기는 _ 로 대체 (메세지 X)

	CANCEL_BY_NOMONEY("&b&l[ &f&lServer &b&l] &c돈이 부족하여 요청된 작업이 취소 되었습니다. 필요한 금액: &e%money_need%원"),
	CANCEL_BY_SIZE_CHANGE("&b&l[ &f&lServer &b&l] &c처리 요청된 땅의 크기가 변경되어 있어 작업이 취소되었습니다."),

	
	PLOT_PROTECTED_BLOCK("&b&l[ &f&lServer &b&l] &c클릭 하신 블럭은 땅 멤버/약식멤버 및 소유자만 이용가능합니다."),
	
	NO_MONEY("&b&l[ &f&lServer &b&l] &c돈이 부족합니다. 필요한 금액: &e%money_need%원"),

	NO_NAME_SPACE("&b&l[ &f&lServer &b&l] &c플레이어 이름에는 공백이 있을 수 없습니다."),

	BIOME_HELP_PRICE_DEFAULT("기본땅: 3만원, 작은땅: 30만원, 큰땅: 120만원"),

	BIOME_HELP("&b&l[ &f&lServer &b&l] &7/땅바이옴 <바이옴> - &6%price%원&f을 소모하여 현재 당신이 서있는 땅의 바이옴을 <바이옴>으로 변경합니다."),

	BIOME_HELP_DEFAULT("&b&l[ &f&lServer &b&l] &7/땅바이옴 <바이옴> - 현재 당신이 서있는 땅의 바이옴을 <바이옴>으로 변경합니다. ( 가격: %price% )"),
	
	BIOME_CONFIRM_HELP("&b&l[ &f&lServer &b&l] &7/땅바이옴 작업확인 - 가장 최근에 요청하신 땅 바이옴 변경요청을 수락합니다. (바이옴 변경 실행)"),

	BIOME_INFO("&b&l[ &f&lServer &b&l] &f현재 땅의 바이옴: &6%biome%"),

	BIOME_SET_CONFIRM(
			"&b&l[ &f&lServer &b&l] &f정말로 &6%price%원&f을 소모하여 대지 &6%plot%&f의 바이옴을 &6%biome%&f으로 바이옴 변경 하는것을 원하시면 &6%sec%초&f 안에 &6\"%cmd_confirm%\"&f 명령어를 입력해주세요."), // \"%cmd_confirm%\"

	CANCELED_BIOME_CONFIRM("&b&l[ &f&lServer &b&l] &c최근에 요청된 땅 바이옴 설정 관련 명령어 확인 작업이 취소되었습니다."),

	BIOME_SET("&b&l[ &f&lServer &b&l] &6%price%&f원을 지불하여, 대지의 바이옴을 &6%biome%&f(으)로 변경하였습니다. "),
	BIOME_SET_CONSOLE("[Server] %player%님이 %price%원을 지불하여 %plot%의 바이옴을 %biome%(으)로 변경하였습니다."),

	
	
	DELETE_PLOT_HELP("&b&l[ &f&lServer &b&l] &7/땅삭제 - &6%price%원&f을 소모하여 현재 서있는 땅을 삭제합니다. (소유권 포기)"),
	DELETE_PLOT_HELP_DEFAULT("&b&l[ &f&lServer &b&l] &7/땅삭제 - 현재 서있는 땅을 삭제합니다. ( 가격: %price% )"),

	
	DELETE_HELP_PRICE_DEFAULT("기본땅: 1000원, 작은땅: 1만원, 큰땅: 10만원"),
	DELETE_CONFIRM_HELP("&b&l[ &f&lServer &b&l] &7/땅삭제 작업확인 - 가장 최근에 요청하신 땅 삭제 요청을 수락합니다. (땅 삭제 실행)"),

	DELETE_PLOT_CONFIRM(
			"&b&l[ &f&lServer &b&l] &f정말로 &6%price%원&f을 소모하여 대지 &6%plot%&f를(을) 초기화 및 소유권 포기하시기를 원하시면 &6%sec%초&f 안에 &6\"%cmd_confirm%\"&f 명령어를 입력해주세요."),

	CANCELED_DELETE_CONFIRM("&b&l[ &f&lServer &b&l] &c최근에 요청된 땅 삭제 관련 명령어 확인 작업이 취소되었습니다."),

	DELETED_PLOT("&b&l[ &f&lServer &b&l] &6%price%원&f을 지불하여, 해당 땅을 초기화 및 소유권 포기하였습니다. 소요 시간: %time%"),
	DELETED_PLOT_CONSOLE("[Server] %player%님이 %price%원을 지불하여 %plot%을 삭제하였습니다. 소요 시간: %time%"),

	
	CLEAR_CONFIRM_HELP("&b&l[ &f&lServer &b&l] &7/땅초기화 작업확인 - 가장 최근에 요청하신 땅 초기화 요청을 수락합니다. (땅 초기화 실행)"),

	CLEAR_PLOT_CONFIRM(
			"&b&l[ &f&lServer &b&l] &f정말로 &6%price%원&f을 소모하여 대지 &6%plot%&f를 초기화 하시는 것을 원하시면 &6%sec%초 &f안에 &6\"%cmd_confirm%\"&f 명령어를 입력해주세요. "),

	CANCELED_CLEAR_CONFIRM("&b&l[ &f&lServer &b&l] &c최근에 요청된 땅 초기화 관련 명령어 확인 작업이 취소되었습니다."),

	CLEAR_PLOT("&b&l[ &f&lServer &b&l] &6%price%원&f을 소모하여 땅을 초기화 하였습니다. 소요 시간: %time%"),
	CLEAR_PLOT_CONSOLE("[Server] &6%player%님이 &6%price%원을 지불하여 &6%plot%을 초기화하였습니다. 소요 시간: %time%"),

	CLEAR_PLOT_HELP("&b&l[ &f&lServer &b&l] &7/땅초기화 - &6%price%원&f을 소모하여 땅을 초기화 합니다."),

	CLEAR_HELP_PRICE_DEFAULT("기본땅: 1만원, 작은땅: 50만원, 큰땅: 200만원"),

	CLEAR_PLOT_HELP_DEFAULT("&b&l[ &f&lServer &b&l] &7/땅초기화 &f- 땅을 초기화 합니다. ( 가격: %price% )"),

	ADD_MEMBER_PRICE_DEFAULT("기본땅: 7500원, 작은땅: 75,000원, 큰땅: 45만원"),

	ADD_MEMBER_HELP("&b&l[ &f&lServer &b&l] &7/땅약식멤버 <닉네임> - &6%price%원&f을 소모하여 <닉네임>님을 현재 당신이 서있는 땅의 약식멤버로 추가합니다."),

	ADD_MEMBER_HELP_DEFAULT(
			"&b&l[ &f&lServer &b&l] &7/땅약식멤버 <닉네임> - <닉네임>님을 현재 당신이 서있는 땅의 약식멤버로 추가합니다. ( 가격: %price% )"),

	
	ADD_CONFIRM_HELP("&b&l[ &f&lServer &b&l] &7/땅약식멤버 작업확인 - 가장 최근에 요청하신 땅 약식멤버 추가 요청을 수락합니다. (땅 약식멤버 추가)"),

	ADD_MEMBER_CONFIRM(
			"&b&l[ &f&lServer &b&l] &f정말로 &6%price%원&f을 소모하여 대지 &6%plot%&f에 &6%target%&f를(을) 약식멤버로 추가 하는것을 원하시면 &6%sec%초 &f안에 &6\"%cmd_confirm%\"&f 명령어를 입력해주세요. "),

	
	REMOVE_MEMBER_CONFIRM(
			"&b&l[ &f&lServer &b&l] &f정말로 대지 &6%plot%&f에 &6%target%&f를(을) 약식멤버에서 추방 하는것을 원하시면 &6%sec%초 &f안에 &6\"%cmd_confirm%\"&f 명령어를 입력해주세요. "),
	
	UNTRUST_CONFIRM(
			"&b&l[ &f&lServer &b&l] &f정말로 대지 &6%plot%&f에 &6%target%&f를(을) 멤버에서 추방 하는것을 원하시면 &6%sec%초 &f안에 &6\"%cmd_confirm%\"&f 명령어를 입력해주세요. "),
	
		
	CANCELED_UNTRUST_CONFIRM("&b&l[ &f&lServer &b&l] &c최근에 요청된 땅 멤버 추방 명령어 확인 작업이 취소되었습니다."),

	CANCELED_REMOVE_MEMBER_CONFIRM("&b&l[ &f&lServer &b&l] &c최근에 요청된 땅 약식멤버 추방 명령어 확인 작업이 취소되었습니다."),

	
	
	PLOT_REMOVE_HELP("&b&l[ &f&lServer &b&l] &7/땅해제 차단 <닉네임> &f- 현재 서 있는 땅의 차단목록에서 <닉네임>님을 차단해제합니다.\n"
			+ "&7/땅해제 멤버 <닉네임> &f- 현재 서 있는 땅의 맴버 목록에서 <닉네임>님을 추방합니다.\n"

			+ "&7/땅해제 약식멤버 <닉네임> &f- 현재 서 있는 땅의 약식맴버 목록에서 <닉네임>님을 추방합니다."),

	PLOT_UNDENY_HELP("&b&l[ &f&lServer &b&l] &7/땅해제 차단 <닉네임> &f- 현재 서 있는 땅의 차단목록에서 <닉네임>님을 차단해제합니다."),
	PLOT_UNTRUST_HELP("&b&l[ &f&lServer &b&l] &7/땅해제 멤버 <닉네임> &f- 현재 서 있는 땅의 맴버 목록에서 <닉네임>님을 추방합니다."),
	PLOT_REMOVE_MEMBER_HELP("&b&l[ &f&lServer &b&l] &7/땅해제 약식멤버 <닉네임> &f- 현재 서 있는 땅의 약식맴버 목록에서 <닉네임>님을 추방합니다."),

	
	PLOT_UNDENY("&b&l[ &f&lServer &b&l] &f대지 &6%plot%&f의 차단목록에서 &6%target%&f님을 차단해제 하였습니다."),

	PLOT_UNDENY_CONSOLE("[Server] %player%님이 %plot%의 차단목록에서 %target%님을 차단해제 하였습니다."),

	
	
	PLOT_UNTRUST("&b&l[ &f&lServer &b&l] &f대지 &6%plot%&f의 멤버목록에서 &6%target%&f님을 추방하였습니다."),

	PLOT_UNTRUST_CONSOLE("[Server] %player%님이 %plot%에 %target%님을 멤버에서 추방하였습니다."),
	
	
	PLOT_REMOVE_MEMBER("&b&l[ &f&lServer &b&l] &f대지 &6%plot%&f의 약식멤버에서 &6%target%&f님을  추방하였습니다."),

	PLOT_REMOVE_MEMBER_CONSOLE("[Server] %player%님이 %plot%의 약식멤버에서  %target%님을 추방하였습니다."),
	
	

	CANCELED_ADD_CONFIRM("&b&l[ &f&lServer &b&l] &c최근에 요청된 땅 약식멤버 추가 명령어 확인 작업이 취소되었습니다."),

	ADD_MEMBER("&b&l[ &f&lServer &b&l] &6%price%원&f을 소모하여 대지 &6%plot%&f에 &6%target%&f님을 약식멤버로 추가 하였습니다."),

	ADD_MEMBER_CONSOLE("[Server] %player%님이 %price%원을 지불하여 %plot%에 약식멤버 %target%님을 추가하였습니다."),

	
	TRUST_CONFIRM_HELP("&b&l[ &f&lServer &b&l] &7/땅멤버 작업확인 - 가장 최근에 요청하신 땅 멤버 추가 요청을 수락합니다. (땅 멤버 추가)"),

	ADD_TRUSTED_CONFIRM(
			"&b&l[ &f&lServer &b&l] &f정말로 &6%price%원&f을 소모하여 &6%target%&f님을 멤버로 추가 하는것을 원하시면 &6%sec%초 &f안에 &6\"%cmd_confirm%\"&f 명령어를 입력해주세요. "),

	CANCELED_TRUST_CONFIRM("&b&l[ &f&lServer &b&l] &c최근에 요청된 땅 멤버 추가 관련 실행 확인 작업이 취소되었습니다."),

	ADD_TRUSTED_PRICE_DEFAULT("기본땅: 5만원, 작은땅: 8만원, 큰땅: 50만원"),

	ADD_TRUSTED_HELP("&b&l[ &f&lServer &b&l] &7/땅멤버 <닉네임> - &6%price%원&f을 소모하여 <닉네임>님을 현재 당신이 서있는 땅의 멤버로 추가합니다."),
	ADD_TRUSTED_HELP_DEFAULT("&b&l[ &f&lServer &b&l] &7/땅멤버 <닉네임> - <닉네임>님들 현재 당신이 서있는 땅의 멤버로 추가합니다. ( 가격: %price% )"),

	ADD_TRUSTED("&b&l[ &f&lServer &b&l] &f&6%price%원&f을 소모하여 대지 &6%plot%&f에 &6%target%&f님을 멤버로 추가 하였습니다."),

	ADD_TRUSTED_CONSOLE("[Server] %player%님이 %price%원을 지불하여 %plot%에 %target%님을 멤버로 추가하였습니다."),

	MIGRATE_HELP("&b&l[ &f&lServer &b&l] &f/땅계정이전 <기존닉네임> <새닉네임>"),
	DELETE_EXPIRED_HELP("&b&l[ &f&lServer &b&l] &f/만기일자지난땅청소"),

	
	DELETE_EXPIRED_INFO("&b&l[ &f&lServer &b&l] &f삭제된 만기일자가 지난 땅 목록 (소오 시간 %time% ms): \n" +
	"&f%deleted_plots%"
			),
	
	
	DELETE_EXPIRED_FAILED_RUNNING("&b&l[ &f&lServer &b&l] &f만기일자가 지난 땅중 다른 작업중에 있어서 삭제되지 않은 땅 목록: \n"
			+ "&f%failed_plots%"
			),
	
	MIGRATED_OWNER(
			"&b&l[ &f&lServer &b&l] &f%from%님이 소유중인 땅의 명의가 %to%님으로 이전되었습니다. 이전된 땅 목록(병합된 땅은 제외됨):\n" 
			+ "%plotlist%"),

	MIGRATED_OWNER_CONSOLE("[Server] %from%님이 소유중인 땅의 명의가 %to%님으로 이전되었습니다. 이전된 땅 목록(병합된 땅은 제외됨):\n"
	+ "%plotlist%"),

	MIGRATED_MEMBER("&b&l[ &f&lServer &b&l] &f%from%님이 약식멤버인 땅에 %to%님이 약식멤버로 추가되었습니다. 처리 된 땅 목록:\n"
	+ "%plotlist%"),

	MIGRATED_MEMBER_CONSOLE("[Server] %from%님이 약식멤버인 땅에 %to%님이 약식멤버로 추가되었습니다. 처리 된 땅 목록:\n"
	+ "%plotlist%"),

	MIGRATED_TRUSTED("&b&l[ &f&lServer &b&l] &f%from%님이 멤버인 땅에 %to%님이 멤버로 추가되었습니다. 처리 된 땅 목록:\n"
	+ "%plotlist%"),

	MIGRATED_TRUSTED_CONSOLE("[Server] %from%님이 멤버인 땅에 %to%님이 멤버로 추가되었습니다. 처리 된 땅 목록: \n"
	+ "%plotlist%"),

	NOT_REQUESTED_CONFIRM("&b&l[ &f&lServer &b&l] &c해당 명령어에 대한 요청된 확인 작업이 없습니다."),

	INGAME_ONLY("[ Server ] 해당 명령어는 게임 내 플레이어만 사용이 가능한 명령어 입니다."),

	NOT_BOOlEAN("&b&l[ &f&lServer &b&l] &c%arg% 번째 인자의 문자열은 true 혹은 false 여야 합니다. "),

	NO_PERM("&b&l[ &f&lServer &b&l] &c권한이 없습니다."),

	PlAYER_NOT_ONLINE("&b&l[ &f&lServer &b&l] &c%target%님은 온라인이 아닙니다."),

	PlAYERNOTFOUND("&b&l[ &f&lServer &b&l] &c%player%님을 찾을 수 없습니다. 서버에 1번 이상 접속한 유저인지 확인해주세요."),

	CONFIG_NOT_SET("&b&l[ &f&lServer &b&l] &c해당 명령어 관련 설정이 누락됬거나 오류가 있습니다. 관리자에게 연락바랍니다."),
	CONFIG_NOT_SET_CONSOLE("[Mine Plot CMD] %config_node% 의 값이 누락됬거나 잘못 설정되어 있습니다."),

	NOT_YOUR_PLOT("&b&l[ &f&lServer &b&l] &c해당 대지는 당신의 땅이 아닙니다."),
	NOT_ALLOWED_PLOT("&b&l[ &f&lServer &b&l] &c당신은 해당 대지의 파티원이나 주인이 아닙니다."),
	PLOT_OWNER_NOT_SET("&b&l[ &f&lServer &b&l] &c해당 대지는 아직 소유자가 없습니다."),
	NOT_IN_PLOT("&b&l[ &f&lServer &b&l] &c현재 당신이 계신 위치는 대지 안이 아닙니다. 대지 세계가 맞는지, 대지 안에 있는지 확인해주세요."),

	
	ALREADY_UNDENIED("&b&l[ &f&lServer &b&l] &c%target%님은 이미 차단 해제되어 있습니다."),
	NOT_MEMBER("&b&l[ &f&lServer &b&l] &c%target%님은 약식멤버 목록에 없습니다."),
	NOT_TRUSTED("&b&l[ &f&lServer &b&l] &c%target%님은 멤버 목록에 없습니다."),

	ALREADY_ADDED("&b&l[ &f&lServer &b&l] &c%target%님은 이미 추가되어 있습니다."),
	ALREADY_OWNER("&b&l[ &f&lServer &b&l] &c%target%님은 해당 땅의 소유자입니다. 따라서 추가가 불필요 합니다."),
	
	
	
	KEEP_PLOT_HELPS("&b&l[ &f&lServer &b&l] &7/땅보존기간 조회/확인 - &f현재 서 있는 땅의 땅 만기일자를 확인합니다.\n"
						
			+ "&b&l[ &f&lServer &b&l] &7/땅보존기간 갱신 - &f현재 서 있는 땅의 땅 만기일자를 갱신합니다."),


	PLOT_LIST_MINE_HELP("&b&l[ &f&lServer &b&l] &7/땅목록 &f- 당신이 이용 가능한 땅의 목록을 확인합니다.\n"
			+ " &f본인이 사용 가능한 목록 중에서 2페이지 이상 조회는 &7\"/땅목록 %player% <페이지>\"&f 명령어로 가능합니다."),
	PLOT_LIST_OTHER_HELP( "&b&l[ &f&lServer &b&l] &7/땅목록 <닉네임> <페이지> &f- <닉네임>님이 이용 가능한 땅의 목록을 확인합니다. &7사용 예시: /땅목록 playerName 1"),
	
	
	PAGE_VALUE_NOT_SET( "&b&l[ &f&lServer &b&l] &c페이지 값이 누락되어 있습니다."),
	OUT_BOUND_PAGE("&b&l[ &f&lServer &b&l] &c%page%는 페이지 범위 밖입니다. &f페이지 범위: %minPage% ~ %maxPage%"),
	NOT_PAGE_NUMBER("&b&l[ &f&lServer &b&l] &c%value%는(은) 페이지 값이 아닙니다. 페이지 값은 0보다 큰 양의 정수여야 합니다."),
	
			
	PLOT_LIST_HEADER("&b&l[ &f&lServer &b&l] &7(페이지 %page%&7) &b%player%&f님의 이용가능한 대지목록 (%size%개의 플롯):"),
	PLOT_INFO_FOR_LIST("&7[%index%&7] %plotID%&r &7- %owner% &6만기일자:%expire_date% &6판매가격: &7%sell_price%"),
	
	PLOT_LIST_FOOTER("%previous% &r&f| %next%"),

	NEXT_PAGE("다음페이지"),
	PREVIOUS_PAGE("이전페이지"),
	
	PAGE_EXIST_COLOR("&6&n"),
	PAGE_NONEXIST_COLOR("&7"),
	CURRENT_PAGE("&6%current_page%&7/&6%max_page%"),
	
	LIST_VISIT_CMD("/plot visit %plot%"),
	LIST_INFO_CMD("/땅정보 %plot%"),
	
	HOVER_LIST_VISIT_CMD("&f/plot visit %plot%"),
	
	HOVER_PLOT_INFO("&6멤버(신뢰됨): %trusted%\n"
			+ "&6약식멤버: %members%\n"
			+ "&6플래그: &7%flags%"),
	
	HOVER_ISONLINE("%isOnline%"),
	ONLINE("&9온라인"),
	OFFLINE("&c오프라인"),
	UNKNOWN("&c알수없음"),
	
	INDEX("&7[%index%]&r"),
	INDEX_NUMBER_COLOR("&6"),
	LIST_PLOTID_COLOR("&6"),
	LIST_OWNER_COLOR("&6"),

	VALUE_COLOR("&7"),
	EXPIRED_COLOR("&c"),
	NOT_EXPIRED_COLOR("&7"),
	
	
	KEEP_PLOT_INFO("&b&l[ &f&lServer &b&l] &f해당 대지의 땅 만기일자 : %expire_date%"),

	KEEP_PLOT_UPDATED("&b&l[ &f&lServer &b&l] &f%plot%의 유지기간 설정을 %expire_date%까지로 갱신됬습니다."),
	
	KEEP_PLOT_UPDATED_CONSOLE("[ Server ] %plot%의 유지기간 설정을 %expire_date%까지로 갱신됬습니다."),
	
	NOT_NEED_UPDATE_EXPIRE_DATE("&b&l[ &f&lServer &b&l] &c해당 땅은 이미 만기일자가 갱신될 만기일자보다 미래임에 따라 갱신이 불필요합니다. &f만기일자:%expire_date%"),

	
	EMPTY_LIST("없음"),
		
	PLOT_INFO_HELP("&b&l[ &f&lServer &b&l] &7/땅정보 &f- 현재 서있는 땅의 정보를 확인합니다.\n"+
			"&b&l[ &f&lServer &b&l] &7/땅정보 <월드;ID> &f- <월드|ID>의 땅의 정보를 확인합니다."),
	
		
	DATE_FORMAT("yyyy년 MM월 dd일"),
	FOREVER("&a무기한/평생"),
	NOT_FOR_SELL("판매 중이 아님"),
	PLOT_INFO("&8&m-------------&r&6정보 &8&m------------\n"
			+ "&6ID: &7%ID% &6소유자: &7%owner% &6바이옴: &7%biome%\n"
			+ "&6만기일자: &7%expire_date% &6판매가: &7%sell_price%\n"
			+ "&6건축가능: &7%canBuild% &6평가: &7%rate%\n"
			+ "&6멤버(신뢰됨): &7%trusted%\n"
			+ "&6약식멤버: &7%members%\n"
			+ "&6차단됨: &7%denied%\n"
			+ "&6단축(이름): &7%ailas%\n"
			+ "&6플래그(설정): &7%flags%\n"
			+ "&8&m-------------&r&6정보 &8&m------------"),
			
			


	PLOTCHAT_MUTED("&b&l[ &f&lServer &b&l] &c당신은 채팅금지 상태입니다."),

	PLOTCHAT_NO_PLAYERS("&b&l[ &f&lServer &b&l] &c당신이 서 있는 땅에는 당신밖에 플레이어가 없습니다."),
	PLOT_CHAT_HELP("&b&l[ &f&lServer &b&l] &7/%command% <채팅내용> &f- 현재 당신이 서 있는 땅에 있는 유저들에게 채팅을 보냅니다."),
	PLOT_CHAT_SPY_ENABLED("&b&l[ &f&lServer &b&l] &f플롯 지역 채팅 스파이 모드가 활성화 됬습니다."),
	PLOT_CHAT_SPY_DISABLED("&b&l[ &f&lServer &b&l] &f플롯 지역 채팅 스파이 모드가 비활성화 됬습니다."),
	
	PLOT_CHAT_MSG_FORMAT("&b[땅 채팅] %displayName% &7: &f%msg%"),
	PLOT_CHAT_SPY_MSG_FORAMT("&7[땅 채팅 스파이] %plotID% %player% : &f%msg%"),;

	private final String def;

	Lang(String def) {
		this.def = def;
	}

	public static void init(ConfigurationSection section) {
		for (Lang lang : Lang.values()) {

			String key = lang.key();
			if (!section.contains(key)) {
				section.set(key, lang.def);
			}
		}

	}

	public String toString(ConfigurationSection lang) {
		return ChatColor.translateAlternateColorCodes('&', lang.getString(key(), def));
	}

	@Override
	public String toString() {
		return toString(Main.get().getLangConfig());
	}

	private String key() {
		return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_HYPHEN, name());
	}

	
	public static String withPlaceHolder(Lang lang, String[] placeholders, Object... values) {

		StringBuffer msg = new StringBuffer(lang.toString());
		for (int i = 0; i < placeholders.length; i++) {
			MCUtils.replaceAll(msg, placeholders[i], String.valueOf(values[i]));
		}

		return msg.toString();

	}
	
}
