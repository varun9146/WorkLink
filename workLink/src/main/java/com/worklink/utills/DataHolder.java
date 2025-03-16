package com.worklink.utills;

public class DataHolder<D, N> {
	private D data;
	private N nextData;

	public DataHolder(D data, N nextData) {
		this.data = data;
		this.nextData = nextData;
	}

	public D getData() {
		return data;
	}

	public N getNextData() {
		return nextData;
	}

	public void setData(D data) {
		this.data = data;
	}

	public void setNextData(N nextData) {
		this.nextData = nextData;
	}

	@Override
	public String toString() {
		return "DataHolder [data=" + data + ", nextData=" + nextData + "]";
	}

}
