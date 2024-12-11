package edu.boun.edgecloudsim.utils;

public class MobileProperties {

    private int ram;
    private int cores;
    private int storage;
    private int mips;
    
	public MobileProperties(int ram, int cores, int storage, int mips) {
		super();
		this.ram = ram;
		this.cores = cores;
		this.storage = storage;
		this.mips = mips;
	}

	public int getRam() {
		return ram;
	}

	public void setRam(int ram) {
		this.ram = ram;
	}

	public int getCores() {
		return cores;
	}

	public void setCores(int cores) {
		this.cores = cores;
	}

	public int getStorage() {
		return storage;
	}

	public void setStorage(int storage) {
		this.storage = storage;
	}

	public int getMips() {
		return mips;
	}

	public void setMips(int mips) {
		this.mips = mips;
	}

	@Override
	public String toString() {
		return "MobileProperties [ram=" + ram + ", cores=" + cores + ", storage=" + storage + ", mips=" + mips + "]";
	}


}
