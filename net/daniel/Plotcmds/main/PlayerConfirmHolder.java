package net.daniel.Plotcmds.main;

import java.util.UUID;

public class PlayerConfirmHolder {

	public BiomeConfirm biome;
	public AddConfirm add;
	public TrustConfirm trust;
	public ClearConfirm clear;
	public DeleteConfirm delete;
	public RemoveTrustConfirm removeTrust;
	public RemoveMemberConfirm removeMember;

	public PlayerConfirmHolder() {
		biome = new BiomeConfirm();
		add = new AddConfirm();
		trust = new TrustConfirm();
		clear = new ClearConfirm();
		delete = new DeleteConfirm();
		removeTrust = new RemoveTrustConfirm();
		removeMember = new RemoveMemberConfirm();
	}

	public class RemoveTrustConfirm extends ConfirmHolder {
		public UUID uuid;

		public String nick;

		@Override
		public boolean useConfirm() {

			return Main.useConfirm_unTrust;
		}

		@Override
		public String getCancelMsg() {

			return Lang.CANCELED_UNTRUST_CONFIRM.toString();
		}

		RemoveTrustConfirm() { // default Constructor
			super();

		}

	}

	public class RemoveMemberConfirm extends ConfirmHolder {
		public UUID uuid;

		public String nick;

		@Override
		public boolean useConfirm() {

			return Main.useConfirm_removeMember;
		}

		@Override
		public String getCancelMsg() {

			return Lang.CANCELED_REMOVE_MEMBER_CONFIRM.toString();
		}

		RemoveMemberConfirm() { // default Constructor
			super();

		}

	}

	public class BiomeConfirm extends ConfirmHolder {

		public String biome;

		@Override
		public boolean useConfirm() {

			return Main.useConfirm_Biome;
		}

		@Override
		public String getCancelMsg() {

			return Lang.CANCELED_BIOME_CONFIRM.toString();
		}

		BiomeConfirm() { // default Constructor
			super();

		}

	}

	public class AddConfirm extends ConfirmHolder {

		public UUID uuid;
		public String nick;


		@Override
		public String getCancelMsg() {

			return Lang.CANCELED_ADD_CONFIRM.toString();
		}

		@Override
		public boolean useConfirm() {

			return Main.useConfirm_Add;
		}

		AddConfirm() { // default Constructor
			super();

		}

	}

	public class TrustConfirm extends ConfirmHolder {

		public UUID uuid;
		public String nick;

		@Override
		public String getCancelMsg() {

			return Lang.CANCELED_TRUST_CONFIRM.toString();
		}

		@Override
		public boolean useConfirm() {

			return Main.useConfirm_Trust;
		}

		TrustConfirm() { // default Constructor
			super();

		}

	}

	public class ClearConfirm extends ConfirmHolder {

		public int size;

		@Override
		public String getCancelMsg() {

			return Lang.CANCELED_CLEAR_CONFIRM.toString();
		}

		@Override
		public boolean useConfirm() {

			return Main.useConfirm_Clear;
		}

		ClearConfirm() { // default Constructor
			super();
		}

	}

	public class DeleteConfirm extends ConfirmHolder {
		@Override
		public String getCancelMsg() {

			return Lang.CANCELED_DELETE_CONFIRM.toString();
		}

		@Override
		public boolean useConfirm() {

			return Main.useConfirm_delete;
		}

		DeleteConfirm() { // default Constructor
			super();

		}

	}

}
