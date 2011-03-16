package edu.fit.cs.sno.snes.rom;

import edu.fit.cs.sno.util.Log;

public class RomInfo {
	boolean lorom = true;
	String gameTitle;
	byte romSpeed;
	byte bankSize; //0 for lorom, 1 for hirom
	byte romType;
	byte romSize;
	byte sramSize;
	byte country;
	byte license;
	int inverseChecksum;
	byte gameVersion;
	public int checksum;
	
	int fileSize;
	int mbits;
	public Boolean isHiROM(){
		return !lorom;
	}
	private static String licenses[] = new String[256];	
	private static String countries[] = {
			"Japan",
			"USA",
			"Australia",
			"Sweden",
			"Finland",
			"Denmark",
			"France",
			"Holland",
			"Spain",
			"Germany",
			"Italy",
			"China",
			"Indonesia",
			"Korea"
	};
	private static String romTypes[] = new String[256];
	private static String romSpeeds[] = {
		"SlowROM","Error","Error","FastROM","Error"
	};
	private static String bankSizes[] = { "LoROM","HiROM","Error","Error" };
	
	
	public static void printRomInfo(RomInfo ri)
	{
		Log.debug("====Rom Information====");
		if (ri.lorom) Log.debug("LoRom detected!");
		else          Log.debug("HiRom detected!");
		Log.debug(String.format("File Size:         %d (%d mbits)",         ri.fileSize, ri.mbits));
		Log.debug(String.format("Title:             %s",                   ri.gameTitle));
		Log.debug(String.format("Bank Size:         %-3d (%s)",              ri.bankSize & 0xFF, bankSizes[ri.bankSize & 0xFF]));
		Log.debug(String.format("Rom Speed:         %-3d (%s)",              ri.romSpeed & 0xFF, romSpeeds[ri.romSpeed & 0xFF]));
		Log.debug(String.format("Rom Type:          %s",             		 romTypes[ri.romType & 0xFF]));			
		Log.debug(String.format("Rom Size:          %-3d (%d Mbit)",         ri.romSize  & 0xFF, 1<<(ri.romSize-7)));
		Log.debug(String.format("Sram Size:         %-3d (%d Kbit)",         ri.sramSize & 0xFF, 1<<(ri.sramSize+3)));
		Log.debug(String.format("Country:           %-3d (%s)",              ri.country  & 0xFF, RomInfo.countries[ri.country & 0xFF]));
		Log.debug(String.format("License:           %-3d (%s)",              ri.license  & 0xFF, licenses[ri.license & 0xFF]));
		Log.debug(String.format("Version:           %-3d",                   ri.gameVersion & 0xFF));
		Log.debug(String.format("Inverse Checksum:  0x%04x (Inverse: 0x%04x)", ri.inverseChecksum,(~ri.inverseChecksum)&0xffff));
		Log.debug(String.format("Checksum:          0x%04x (Inverse: 0x%04x)", ri.checksum, (~ri.checksum)&0xffff));

	}
	
	static {
		for(int i=0; i<256; i++) {
			licenses[i] = "Unknown";
			romTypes[i] = "Invalid ROM type";
		}
		romTypes[0] = "ROM only";
		romTypes[1] = "ROM + RAM";
		romTypes[2] = "ROM + SRAM";
		romTypes[3] = "ROM + DSP1";
		romTypes[4] = "ROM + RAM + DSP1";
		romTypes[5] = "ROM + SRAM + DSP1";
		romTypes[11] = "ROM + CX4 (Megaman X2/X3)";
		romTypes[19] = "ROM + SFX";
		romTypes[227] = "ROM + RAM + GB";
		romTypes[246] = "ROM + DSP2";
		licenses[1] = "Nintendo";
		licenses[3] = "Imagineer-Zoom";
		licenses[5] = "Zamuse";
		licenses[6] = "Falcom";
		licenses[8] = "Capcom";
		licenses[9] = "HOT-B";
		licenses[10] = "Jaleco";
		licenses[11] = "Coconuts";
		licenses[12] = "Rage Software";
		licenses[14] = "Technos";
		licenses[15] = "Mebio Software";
		licenses[18] = "Gremlin Graphics";
		licenses[19] = "Electronic Arts";
		licenses[21] = "COBRA Team";
		licenses[22] = "Human/Field";
		licenses[23] = "KOEI";
		licenses[24] = "Hudson Soft";
		licenses[26] = "Yanoman";
		licenses[28] = "Tecmo";
		licenses[30] = "Open System";
		licenses[31] = "Virgin Games";
		licenses[32] = "KSS";
		licenses[33] = "Sunsoft";
		licenses[34] = "POW";
		licenses[35] = "Micro World";
		licenses[38] = "Enix";
		licenses[39] = "Loriciel/Electro Brain";
		licenses[40] = "Kemco";
		licenses[41] = "Seta Co.,Ltd.";
		licenses[45] = "Visit Co.,Ltd.";
		licenses[49] = "Carrozzeria";
		licenses[50] = "Dynamic";
		licenses[51] = "Nintendo";
		licenses[52] = "Magifact";
		licenses[53] = "Hect";
		licenses[60] = "Empire Software";
		licenses[61] = "Loriciel";
		licenses[64] = "Seika Corp.";
		licenses[65] = "UBI Soft";
		licenses[70] = "System 3";
		licenses[71] = "Spectrum Holobyte";
		licenses[73] = "Irem";
		licenses[75] = "Raya Systems/Sculptured Software";
		licenses[76] = "Renovation Products";
		licenses[77] = "Malibu Games/Black Pearl";
		licenses[79] = "U.S. Gold";
		licenses[80] = "Absolute Entertainment";
		licenses[81] = "Acclaim";
		licenses[82] = "Activision";
		licenses[83] = "American Sammy";
		licenses[84] = "GameTek";
		licenses[85] = "Hi Tech Expressions";
		licenses[86] = "LJN Toys";
		licenses[90] = "Mindscape";
		licenses[93] = "Tradewest";
		licenses[95] = "American Softworks Corp.";
		licenses[96] = "Titus";
		licenses[97] = "Virgin Interactive Entertainment";
		licenses[98] = "Maxis";
		licenses[103] = "Ocean";
		licenses[105] = "Electronic Arts";
		licenses[107] = "Laser Beam";
		licenses[110] = "Elite";
		licenses[111] = "Electro Brain";
		licenses[112] = "Infogrames";
		licenses[113] = "Interplay";
		licenses[114] = "LucasArts";
		licenses[115] = "Parker Brothers";
		licenses[117] = "STORM";
		licenses[120] = "THQ Software";
		licenses[121] = "Accolade Inc.";
		licenses[122] = "Triffix Entertainment";
		licenses[124] = "Microprose";
		licenses[127] = "Kemco";
		licenses[128] = "Misawa";
		licenses[129] = "Teichio";
		licenses[130] = "Namco Ltd.";
		licenses[131] = "Lozc";
		licenses[132] = "Koei";
		licenses[134] = "Tokuma Shoten Intermedia";
		licenses[136] = "DATAM-Polystar";
		licenses[139] = "Bullet-Proof Software";
		licenses[140] = "Vic Tokai";
		licenses[142] = "Character Soft";
		licenses[143] = "I''Max";
		licenses[144] = "Takara";
		licenses[145] = "CHUN Soft";
		licenses[146] = "Video System Co., Ltd.";
		licenses[147] = "BEC";
		licenses[149] = "Varie";
		licenses[151] = "Kaneco";
		licenses[153] = "Pack in Video";
		licenses[154] = "Nichibutsu";
		licenses[155] = "TECMO";
		licenses[156] = "Imagineer Co.";
		licenses[160] = "Telenet";
		licenses[164] = "Konami";
		licenses[165] = "K.Amusement Leasing Co.";
		licenses[167] = "Takara";
		licenses[169] = "Technos Jap.";
		licenses[170] = "JVC";
		licenses[172] = "Toei Animation";
		licenses[173] = "Toho";
		licenses[175] = "Namco Ltd.";
		licenses[177] = "ASCII Co. Activison";
		licenses[178] = "BanDai America";
		licenses[180] = "Enix";
		licenses[182] = "Halken";
		licenses[186] = "Culture Brain";
		licenses[187] = "Sunsoft";
		licenses[188] = "Toshiba EMI";
		licenses[189] = "Sony Imagesoft";
		licenses[191] = "Sammy";
		licenses[192] = "Taito";
		licenses[194] = "Kemco";
		licenses[195] = "Square";
		licenses[196] = "Tokuma Soft";
		licenses[197] = "Data East";
		licenses[198] = "Tonkin House";
		licenses[200] = "KOEI";
		licenses[202] = "Konami USA";
		licenses[203] = "NTVIC";
		licenses[205] = "Meldac";
		licenses[206] = "Pony Canyon";
		licenses[207] = "Sotsu Agency/Sunrise";
		licenses[208] = "Disco/Taito";
		licenses[209] = "Sofel";
		licenses[210] = "Quest Corp.";
		licenses[211] = "Sigma";
		licenses[214] = "Naxat";
		licenses[216] = "Capcom Co., Ltd.";
		licenses[217] = "Banpresto";
		licenses[218] = "Tomy";
		licenses[219] = "Acclaim";
		licenses[221] = "NCS";
		licenses[222] = "Human Entertainment";
		licenses[223] = "Altron";
		licenses[224] = "Jaleco";
		licenses[226] = "Yutaka";
		licenses[228] = "T&ESoft";
		licenses[229] = "EPOCH Co.,Ltd.";
		licenses[231] = "Athena";
		licenses[232] = "Asmik";
		licenses[233] = "Natsume";
		licenses[234] = "King Records";
		licenses[235] = "Atlus";
		licenses[236] = "Sony Music Entertainment";
		licenses[238] = "IGS";
		licenses[241] = "Motown Software";
		licenses[242] = "Left Field Entertainment";
		licenses[243] = "Beam Software";
		licenses[244] = "Tec Magik";
		licenses[249] = "Cybersoft";
	}

}
