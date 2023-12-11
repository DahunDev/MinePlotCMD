package net.daniel.Plotcmds.main;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

import com.google.common.base.CaseFormat;

import net.daniel.plotcmd.Utils.MCUtils;

public enum Lang {
	// Class ���� ���ϸ�� ���� �ʿ�� ����
	// �̸��� ���Ͽ� �ִ� �޼����� ������, ����� _ �� ��ü (�޼��� X)

	CANCEL_BY_NOMONEY("&b&l[ &f&lServer &b&l] &c���� �����Ͽ� ��û�� �۾��� ��� �Ǿ����ϴ�. �ʿ��� �ݾ�: &e%money_need%��"),
	CANCEL_BY_SIZE_CHANGE("&b&l[ &f&lServer &b&l] &có�� ��û�� ���� ũ�Ⱑ ����Ǿ� �־� �۾��� ��ҵǾ����ϴ�."),

	
	PLOT_PROTECTED_BLOCK("&b&l[ &f&lServer &b&l] &cŬ�� �Ͻ� ���� �� ���/��ĸ�� �� �����ڸ� �̿밡���մϴ�."),
	
	NO_MONEY("&b&l[ &f&lServer &b&l] &c���� �����մϴ�. �ʿ��� �ݾ�: &e%money_need%��"),

	NO_NAME_SPACE("&b&l[ &f&lServer &b&l] &c�÷��̾� �̸����� ������ ���� �� �����ϴ�."),

	BIOME_HELP_PRICE_DEFAULT("�⺻��: 3����, ������: 30����, ū��: 120����"),

	BIOME_HELP("&b&l[ &f&lServer &b&l] &7/�����̿� <���̿�> - &6%price%��&f�� �Ҹ��Ͽ� ���� ����� ���ִ� ���� ���̿��� <���̿�>���� �����մϴ�."),

	BIOME_HELP_DEFAULT("&b&l[ &f&lServer &b&l] &7/�����̿� <���̿�> - ���� ����� ���ִ� ���� ���̿��� <���̿�>���� �����մϴ�. ( ����: %price% )"),
	
	BIOME_CONFIRM_HELP("&b&l[ &f&lServer &b&l] &7/�����̿� �۾�Ȯ�� - ���� �ֱٿ� ��û�Ͻ� �� ���̿� �����û�� �����մϴ�. (���̿� ���� ����)"),

	BIOME_INFO("&b&l[ &f&lServer &b&l] &f���� ���� ���̿�: &6%biome%"),

	BIOME_SET_CONFIRM(
			"&b&l[ &f&lServer &b&l] &f������ &6%price%��&f�� �Ҹ��Ͽ� ���� &6%plot%&f�� ���̿��� &6%biome%&f���� ���̿� ���� �ϴ°��� ���Ͻø� &6%sec%��&f �ȿ� &6\"%cmd_confirm%\"&f ��ɾ �Է����ּ���."), // \"%cmd_confirm%\"

	CANCELED_BIOME_CONFIRM("&b&l[ &f&lServer &b&l] &c�ֱٿ� ��û�� �� ���̿� ���� ���� ��ɾ� Ȯ�� �۾��� ��ҵǾ����ϴ�."),

	BIOME_SET("&b&l[ &f&lServer &b&l] &6%price%&f���� �����Ͽ�, ������ ���̿��� &6%biome%&f(��)�� �����Ͽ����ϴ�. "),
	BIOME_SET_CONSOLE("[Server] %player%���� %price%���� �����Ͽ� %plot%�� ���̿��� %biome%(��)�� �����Ͽ����ϴ�."),

	
	
	DELETE_PLOT_HELP("&b&l[ &f&lServer &b&l] &7/������ - &6%price%��&f�� �Ҹ��Ͽ� ���� ���ִ� ���� �����մϴ�. (������ ����)"),
	DELETE_PLOT_HELP_DEFAULT("&b&l[ &f&lServer &b&l] &7/������ - ���� ���ִ� ���� �����մϴ�. ( ����: %price% )"),

	
	DELETE_HELP_PRICE_DEFAULT("�⺻��: 1000��, ������: 1����, ū��: 10����"),
	DELETE_CONFIRM_HELP("&b&l[ &f&lServer &b&l] &7/������ �۾�Ȯ�� - ���� �ֱٿ� ��û�Ͻ� �� ���� ��û�� �����մϴ�. (�� ���� ����)"),

	DELETE_PLOT_CONFIRM(
			"&b&l[ &f&lServer &b&l] &f������ &6%price%��&f�� �Ҹ��Ͽ� ���� &6%plot%&f��(��) �ʱ�ȭ �� ������ �����Ͻñ⸦ ���Ͻø� &6%sec%��&f �ȿ� &6\"%cmd_confirm%\"&f ��ɾ �Է����ּ���."),

	CANCELED_DELETE_CONFIRM("&b&l[ &f&lServer &b&l] &c�ֱٿ� ��û�� �� ���� ���� ��ɾ� Ȯ�� �۾��� ��ҵǾ����ϴ�."),

	DELETED_PLOT("&b&l[ &f&lServer &b&l] &6%price%��&f�� �����Ͽ�, �ش� ���� �ʱ�ȭ �� ������ �����Ͽ����ϴ�. �ҿ� �ð�: %time%"),
	DELETED_PLOT_CONSOLE("[Server] %player%���� %price%���� �����Ͽ� %plot%�� �����Ͽ����ϴ�. �ҿ� �ð�: %time%"),

	
	CLEAR_CONFIRM_HELP("&b&l[ &f&lServer &b&l] &7/���ʱ�ȭ �۾�Ȯ�� - ���� �ֱٿ� ��û�Ͻ� �� �ʱ�ȭ ��û�� �����մϴ�. (�� �ʱ�ȭ ����)"),

	CLEAR_PLOT_CONFIRM(
			"&b&l[ &f&lServer &b&l] &f������ &6%price%��&f�� �Ҹ��Ͽ� ���� &6%plot%&f�� �ʱ�ȭ �Ͻô� ���� ���Ͻø� &6%sec%�� &f�ȿ� &6\"%cmd_confirm%\"&f ��ɾ �Է����ּ���. "),

	CANCELED_CLEAR_CONFIRM("&b&l[ &f&lServer &b&l] &c�ֱٿ� ��û�� �� �ʱ�ȭ ���� ��ɾ� Ȯ�� �۾��� ��ҵǾ����ϴ�."),

	CLEAR_PLOT("&b&l[ &f&lServer &b&l] &6%price%��&f�� �Ҹ��Ͽ� ���� �ʱ�ȭ �Ͽ����ϴ�. �ҿ� �ð�: %time%"),
	CLEAR_PLOT_CONSOLE("[Server] &6%player%���� &6%price%���� �����Ͽ� &6%plot%�� �ʱ�ȭ�Ͽ����ϴ�. �ҿ� �ð�: %time%"),

	CLEAR_PLOT_HELP("&b&l[ &f&lServer &b&l] &7/���ʱ�ȭ - &6%price%��&f�� �Ҹ��Ͽ� ���� �ʱ�ȭ �մϴ�."),

	CLEAR_HELP_PRICE_DEFAULT("�⺻��: 1����, ������: 50����, ū��: 200����"),

	CLEAR_PLOT_HELP_DEFAULT("&b&l[ &f&lServer &b&l] &7/���ʱ�ȭ &f- ���� �ʱ�ȭ �մϴ�. ( ����: %price% )"),

	ADD_MEMBER_PRICE_DEFAULT("�⺻��: 7500��, ������: 75,000��, ū��: 45����"),

	ADD_MEMBER_HELP("&b&l[ &f&lServer &b&l] &7/����ĸ�� <�г���> - &6%price%��&f�� �Ҹ��Ͽ� <�г���>���� ���� ����� ���ִ� ���� ��ĸ���� �߰��մϴ�."),

	ADD_MEMBER_HELP_DEFAULT(
			"&b&l[ &f&lServer &b&l] &7/����ĸ�� <�г���> - <�г���>���� ���� ����� ���ִ� ���� ��ĸ���� �߰��մϴ�. ( ����: %price% )"),

	
	ADD_CONFIRM_HELP("&b&l[ &f&lServer &b&l] &7/����ĸ�� �۾�Ȯ�� - ���� �ֱٿ� ��û�Ͻ� �� ��ĸ�� �߰� ��û�� �����մϴ�. (�� ��ĸ�� �߰�)"),

	ADD_MEMBER_CONFIRM(
			"&b&l[ &f&lServer &b&l] &f������ &6%price%��&f�� �Ҹ��Ͽ� ���� &6%plot%&f�� &6%target%&f��(��) ��ĸ���� �߰� �ϴ°��� ���Ͻø� &6%sec%�� &f�ȿ� &6\"%cmd_confirm%\"&f ��ɾ �Է����ּ���. "),

	
	REMOVE_MEMBER_CONFIRM(
			"&b&l[ &f&lServer &b&l] &f������ ���� &6%plot%&f�� &6%target%&f��(��) ��ĸ������ �߹� �ϴ°��� ���Ͻø� &6%sec%�� &f�ȿ� &6\"%cmd_confirm%\"&f ��ɾ �Է����ּ���. "),
	
	UNTRUST_CONFIRM(
			"&b&l[ &f&lServer &b&l] &f������ ���� &6%plot%&f�� &6%target%&f��(��) ������� �߹� �ϴ°��� ���Ͻø� &6%sec%�� &f�ȿ� &6\"%cmd_confirm%\"&f ��ɾ �Է����ּ���. "),
	
		
	CANCELED_UNTRUST_CONFIRM("&b&l[ &f&lServer &b&l] &c�ֱٿ� ��û�� �� ��� �߹� ��ɾ� Ȯ�� �۾��� ��ҵǾ����ϴ�."),

	CANCELED_REMOVE_MEMBER_CONFIRM("&b&l[ &f&lServer &b&l] &c�ֱٿ� ��û�� �� ��ĸ�� �߹� ��ɾ� Ȯ�� �۾��� ��ҵǾ����ϴ�."),

	
	
	PLOT_REMOVE_HELP("&b&l[ &f&lServer &b&l] &7/������ ���� <�г���> &f- ���� �� �ִ� ���� ���ܸ�Ͽ��� <�г���>���� ���������մϴ�.\n"
			+ "&7/������ ��� <�г���> &f- ���� �� �ִ� ���� �ɹ� ��Ͽ��� <�г���>���� �߹��մϴ�.\n"

			+ "&7/������ ��ĸ�� <�г���> &f- ���� �� �ִ� ���� ��ĸɹ� ��Ͽ��� <�г���>���� �߹��մϴ�."),

	PLOT_UNDENY_HELP("&b&l[ &f&lServer &b&l] &7/������ ���� <�г���> &f- ���� �� �ִ� ���� ���ܸ�Ͽ��� <�г���>���� ���������մϴ�."),
	PLOT_UNTRUST_HELP("&b&l[ &f&lServer &b&l] &7/������ ��� <�г���> &f- ���� �� �ִ� ���� �ɹ� ��Ͽ��� <�г���>���� �߹��մϴ�."),
	PLOT_REMOVE_MEMBER_HELP("&b&l[ &f&lServer &b&l] &7/������ ��ĸ�� <�г���> &f- ���� �� �ִ� ���� ��ĸɹ� ��Ͽ��� <�г���>���� �߹��մϴ�."),

	
	PLOT_UNDENY("&b&l[ &f&lServer &b&l] &f���� &6%plot%&f�� ���ܸ�Ͽ��� &6%target%&f���� �������� �Ͽ����ϴ�."),

	PLOT_UNDENY_CONSOLE("[Server] %player%���� %plot%�� ���ܸ�Ͽ��� %target%���� �������� �Ͽ����ϴ�."),

	
	
	PLOT_UNTRUST("&b&l[ &f&lServer &b&l] &f���� &6%plot%&f�� �����Ͽ��� &6%target%&f���� �߹��Ͽ����ϴ�."),

	PLOT_UNTRUST_CONSOLE("[Server] %player%���� %plot%�� %target%���� ������� �߹��Ͽ����ϴ�."),
	
	
	PLOT_REMOVE_MEMBER("&b&l[ &f&lServer &b&l] &f���� &6%plot%&f�� ��ĸ������ &6%target%&f����  �߹��Ͽ����ϴ�."),

	PLOT_REMOVE_MEMBER_CONSOLE("[Server] %player%���� %plot%�� ��ĸ������  %target%���� �߹��Ͽ����ϴ�."),
	
	

	CANCELED_ADD_CONFIRM("&b&l[ &f&lServer &b&l] &c�ֱٿ� ��û�� �� ��ĸ�� �߰� ��ɾ� Ȯ�� �۾��� ��ҵǾ����ϴ�."),

	ADD_MEMBER("&b&l[ &f&lServer &b&l] &6%price%��&f�� �Ҹ��Ͽ� ���� &6%plot%&f�� &6%target%&f���� ��ĸ���� �߰� �Ͽ����ϴ�."),

	ADD_MEMBER_CONSOLE("[Server] %player%���� %price%���� �����Ͽ� %plot%�� ��ĸ�� %target%���� �߰��Ͽ����ϴ�."),

	
	TRUST_CONFIRM_HELP("&b&l[ &f&lServer &b&l] &7/����� �۾�Ȯ�� - ���� �ֱٿ� ��û�Ͻ� �� ��� �߰� ��û�� �����մϴ�. (�� ��� �߰�)"),

	ADD_TRUSTED_CONFIRM(
			"&b&l[ &f&lServer &b&l] &f������ &6%price%��&f�� �Ҹ��Ͽ� &6%target%&f���� ����� �߰� �ϴ°��� ���Ͻø� &6%sec%�� &f�ȿ� &6\"%cmd_confirm%\"&f ��ɾ �Է����ּ���. "),

	CANCELED_TRUST_CONFIRM("&b&l[ &f&lServer &b&l] &c�ֱٿ� ��û�� �� ��� �߰� ���� ���� Ȯ�� �۾��� ��ҵǾ����ϴ�."),

	ADD_TRUSTED_PRICE_DEFAULT("�⺻��: 5����, ������: 8����, ū��: 50����"),

	ADD_TRUSTED_HELP("&b&l[ &f&lServer &b&l] &7/����� <�г���> - &6%price%��&f�� �Ҹ��Ͽ� <�г���>���� ���� ����� ���ִ� ���� ����� �߰��մϴ�."),
	ADD_TRUSTED_HELP_DEFAULT("&b&l[ &f&lServer &b&l] &7/����� <�г���> - <�г���>�Ե� ���� ����� ���ִ� ���� ����� �߰��մϴ�. ( ����: %price% )"),

	ADD_TRUSTED("&b&l[ &f&lServer &b&l] &f&6%price%��&f�� �Ҹ��Ͽ� ���� &6%plot%&f�� &6%target%&f���� ����� �߰� �Ͽ����ϴ�."),

	ADD_TRUSTED_CONSOLE("[Server] %player%���� %price%���� �����Ͽ� %plot%�� %target%���� ����� �߰��Ͽ����ϴ�."),

	MIGRATE_HELP("&b&l[ &f&lServer &b&l] &f/���������� <�����г���> <���г���>"),
	DELETE_EXPIRED_HELP("&b&l[ &f&lServer &b&l] &f/��������������û��"),

	
	DELETE_EXPIRED_INFO("&b&l[ &f&lServer &b&l] &f������ �������ڰ� ���� �� ��� (�ҿ� �ð� %time% ms): \n" +
	"&f%deleted_plots%"
			),
	
	
	DELETE_EXPIRED_FAILED_RUNNING("&b&l[ &f&lServer &b&l] &f�������ڰ� ���� ���� �ٸ� �۾��߿� �־ �������� ���� �� ���: \n"
			+ "&f%failed_plots%"
			),
	
	MIGRATED_OWNER(
			"&b&l[ &f&lServer &b&l] &f%from%���� �������� ���� ���ǰ� %to%������ �����Ǿ����ϴ�. ������ �� ���(���յ� ���� ���ܵ�):\n" 
			+ "%plotlist%"),

	MIGRATED_OWNER_CONSOLE("[Server] %from%���� �������� ���� ���ǰ� %to%������ �����Ǿ����ϴ�. ������ �� ���(���յ� ���� ���ܵ�):\n"
	+ "%plotlist%"),

	MIGRATED_MEMBER("&b&l[ &f&lServer &b&l] &f%from%���� ��ĸ���� ���� %to%���� ��ĸ���� �߰��Ǿ����ϴ�. ó�� �� �� ���:\n"
	+ "%plotlist%"),

	MIGRATED_MEMBER_CONSOLE("[Server] %from%���� ��ĸ���� ���� %to%���� ��ĸ���� �߰��Ǿ����ϴ�. ó�� �� �� ���:\n"
	+ "%plotlist%"),

	MIGRATED_TRUSTED("&b&l[ &f&lServer &b&l] &f%from%���� ����� ���� %to%���� ����� �߰��Ǿ����ϴ�. ó�� �� �� ���:\n"
	+ "%plotlist%"),

	MIGRATED_TRUSTED_CONSOLE("[Server] %from%���� ����� ���� %to%���� ����� �߰��Ǿ����ϴ�. ó�� �� �� ���: \n"
	+ "%plotlist%"),

	NOT_REQUESTED_CONFIRM("&b&l[ &f&lServer &b&l] &c�ش� ��ɾ ���� ��û�� Ȯ�� �۾��� �����ϴ�."),

	INGAME_ONLY("[ Server ] �ش� ��ɾ�� ���� �� �÷��̾ ����� ������ ��ɾ� �Դϴ�."),

	NOT_BOOlEAN("&b&l[ &f&lServer &b&l] &c%arg% ��° ������ ���ڿ��� true Ȥ�� false ���� �մϴ�. "),

	NO_PERM("&b&l[ &f&lServer &b&l] &c������ �����ϴ�."),

	PlAYER_NOT_ONLINE("&b&l[ &f&lServer &b&l] &c%target%���� �¶����� �ƴմϴ�."),

	PlAYERNOTFOUND("&b&l[ &f&lServer &b&l] &c%player%���� ã�� �� �����ϴ�. ������ 1�� �̻� ������ �������� Ȯ�����ּ���."),

	CONFIG_NOT_SET("&b&l[ &f&lServer &b&l] &c�ش� ��ɾ� ���� ������ �����ų� ������ �ֽ��ϴ�. �����ڿ��� �����ٶ��ϴ�."),
	CONFIG_NOT_SET_CONSOLE("[Mine Plot CMD] %config_node% �� ���� �����ų� �߸� �����Ǿ� �ֽ��ϴ�."),

	NOT_YOUR_PLOT("&b&l[ &f&lServer &b&l] &c�ش� ������ ����� ���� �ƴմϴ�."),
	NOT_ALLOWED_PLOT("&b&l[ &f&lServer &b&l] &c����� �ش� ������ ��Ƽ���̳� ������ �ƴմϴ�."),
	PLOT_OWNER_NOT_SET("&b&l[ &f&lServer &b&l] &c�ش� ������ ���� �����ڰ� �����ϴ�."),
	NOT_IN_PLOT("&b&l[ &f&lServer &b&l] &c���� ����� ��� ��ġ�� ���� ���� �ƴմϴ�. ���� ���谡 �´���, ���� �ȿ� �ִ��� Ȯ�����ּ���."),

	
	ALREADY_UNDENIED("&b&l[ &f&lServer &b&l] &c%target%���� �̹� ���� �����Ǿ� �ֽ��ϴ�."),
	NOT_MEMBER("&b&l[ &f&lServer &b&l] &c%target%���� ��ĸ�� ��Ͽ� �����ϴ�."),
	NOT_TRUSTED("&b&l[ &f&lServer &b&l] &c%target%���� ��� ��Ͽ� �����ϴ�."),

	ALREADY_ADDED("&b&l[ &f&lServer &b&l] &c%target%���� �̹� �߰��Ǿ� �ֽ��ϴ�."),
	ALREADY_OWNER("&b&l[ &f&lServer &b&l] &c%target%���� �ش� ���� �������Դϴ�. ���� �߰��� ���ʿ� �մϴ�."),
	
	
	
	KEEP_PLOT_HELPS("&b&l[ &f&lServer &b&l] &7/�������Ⱓ ��ȸ/Ȯ�� - &f���� �� �ִ� ���� �� �������ڸ� Ȯ���մϴ�.\n"
						
			+ "&b&l[ &f&lServer &b&l] &7/�������Ⱓ ���� - &f���� �� �ִ� ���� �� �������ڸ� �����մϴ�."),


	PLOT_LIST_MINE_HELP("&b&l[ &f&lServer &b&l] &7/����� &f- ����� �̿� ������ ���� ����� Ȯ���մϴ�.\n"
			+ " &f������ ��� ������ ��� �߿��� 2������ �̻� ��ȸ�� &7\"/����� %player% <������>\"&f ��ɾ�� �����մϴ�."),
	PLOT_LIST_OTHER_HELP( "&b&l[ &f&lServer &b&l] &7/����� <�г���> <������> &f- <�г���>���� �̿� ������ ���� ����� Ȯ���մϴ�. &7��� ����: /����� playerName 1"),
	
	
	PAGE_VALUE_NOT_SET( "&b&l[ &f&lServer &b&l] &c������ ���� �����Ǿ� �ֽ��ϴ�."),
	OUT_BOUND_PAGE("&b&l[ &f&lServer &b&l] &c%page%�� ������ ���� ���Դϴ�. &f������ ����: %minPage% ~ %maxPage%"),
	NOT_PAGE_NUMBER("&b&l[ &f&lServer &b&l] &c%value%��(��) ������ ���� �ƴմϴ�. ������ ���� 0���� ū ���� �������� �մϴ�."),
	
			
	PLOT_LIST_HEADER("&b&l[ &f&lServer &b&l] &7(������ %page%&7) &b%player%&f���� �̿밡���� ������� (%size%���� �÷�):"),
	PLOT_INFO_FOR_LIST("&7[%index%&7] %plotID%&r &7- %owner% &6��������:%expire_date% &6�ǸŰ���: &7%sell_price%"),
	
	PLOT_LIST_FOOTER("%previous% &r&f| %next%"),

	NEXT_PAGE("����������"),
	PREVIOUS_PAGE("����������"),
	
	PAGE_EXIST_COLOR("&6&n"),
	PAGE_NONEXIST_COLOR("&7"),
	CURRENT_PAGE("&6%current_page%&7/&6%max_page%"),
	
	LIST_VISIT_CMD("/plot visit %plot%"),
	LIST_INFO_CMD("/������ %plot%"),
	
	HOVER_LIST_VISIT_CMD("&f/plot visit %plot%"),
	
	HOVER_PLOT_INFO("&6���(�ŷڵ�): %trusted%\n"
			+ "&6��ĸ��: %members%\n"
			+ "&6�÷���: &7%flags%"),
	
	HOVER_ISONLINE("%isOnline%"),
	ONLINE("&9�¶���"),
	OFFLINE("&c��������"),
	UNKNOWN("&c�˼�����"),
	
	INDEX("&7[%index%]&r"),
	INDEX_NUMBER_COLOR("&6"),
	LIST_PLOTID_COLOR("&6"),
	LIST_OWNER_COLOR("&6"),

	VALUE_COLOR("&7"),
	EXPIRED_COLOR("&c"),
	NOT_EXPIRED_COLOR("&7"),
	
	
	KEEP_PLOT_INFO("&b&l[ &f&lServer &b&l] &f�ش� ������ �� �������� : %expire_date%"),

	KEEP_PLOT_UPDATED("&b&l[ &f&lServer &b&l] &f%plot%�� �����Ⱓ ������ %expire_date%������ ���ŉ���ϴ�."),
	
	KEEP_PLOT_UPDATED_CONSOLE("[ Server ] %plot%�� �����Ⱓ ������ %expire_date%������ ���ŉ���ϴ�."),
	
	NOT_NEED_UPDATE_EXPIRE_DATE("&b&l[ &f&lServer &b&l] &c�ش� ���� �̹� �������ڰ� ���ŵ� �������ں��� �̷��ӿ� ���� ������ ���ʿ��մϴ�. &f��������:%expire_date%"),

	
	EMPTY_LIST("����"),
		
	PLOT_INFO_HELP("&b&l[ &f&lServer &b&l] &7/������ &f- ���� ���ִ� ���� ������ Ȯ���մϴ�.\n"+
			"&b&l[ &f&lServer &b&l] &7/������ <����;ID> &f- <����|ID>�� ���� ������ Ȯ���մϴ�."),
	
		
	DATE_FORMAT("yyyy�� MM�� dd��"),
	FOREVER("&a������/���"),
	NOT_FOR_SELL("�Ǹ� ���� �ƴ�"),
	PLOT_INFO("&8&m-------------&r&6���� &8&m------------\n"
			+ "&6ID: &7%ID% &6������: &7%owner% &6���̿�: &7%biome%\n"
			+ "&6��������: &7%expire_date% &6�ǸŰ�: &7%sell_price%\n"
			+ "&6���డ��: &7%canBuild% &6��: &7%rate%\n"
			+ "&6���(�ŷڵ�): &7%trusted%\n"
			+ "&6��ĸ��: &7%members%\n"
			+ "&6���ܵ�: &7%denied%\n"
			+ "&6����(�̸�): &7%ailas%\n"
			+ "&6�÷���(����): &7%flags%\n"
			+ "&8&m-------------&r&6���� &8&m------------"),
			
			


	PLOTCHAT_MUTED("&b&l[ &f&lServer &b&l] &c����� ä�ñ��� �����Դϴ�."),

	PLOTCHAT_NO_PLAYERS("&b&l[ &f&lServer &b&l] &c����� �� �ִ� ������ ��Źۿ� �÷��̾ �����ϴ�."),
	PLOT_CHAT_HELP("&b&l[ &f&lServer &b&l] &7/%command% <ä�ó���> &f- ���� ����� �� �ִ� ���� �ִ� �����鿡�� ä���� �����ϴ�."),
	PLOT_CHAT_SPY_ENABLED("&b&l[ &f&lServer &b&l] &f�÷� ���� ä�� ������ ��尡 Ȱ��ȭ ����ϴ�."),
	PLOT_CHAT_SPY_DISABLED("&b&l[ &f&lServer &b&l] &f�÷� ���� ä�� ������ ��尡 ��Ȱ��ȭ ����ϴ�."),
	
	PLOT_CHAT_MSG_FORMAT("&b[�� ä��] %displayName% &7: &f%msg%"),
	PLOT_CHAT_SPY_MSG_FORAMT("&7[�� ä�� ������] %plotID% %player% : &f%msg%"),;

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
