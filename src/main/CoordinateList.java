package mods.immibis.ars;

import java.util.NoSuchElementException;

public class CoordinateList {
	private int dataSize = 0;
	private int nCoords = 0;
	private int[] data;
	
	public CoordinateList(int initial_capacity) {
		if(initial_capacity < 8)
			initial_capacity = 8;
		data = new int[initial_capacity * 4];
	}
	
	public void add(int x, int y, int z, int mode) {
		if(y < 0 || y > 255)
			return;

		if(data.length == dataSize) {
			int[] newData = new int[data.length * 2];
			System.arraycopy(data, 0, newData, 0, dataSize);
			data = newData;
		}
		
		data[dataSize++] = x;
		data[dataSize++] = y;
		data[dataSize++] = z;
		data[dataSize++] = mode; // not currently used
		nCoords++;
	}
	
	public int size() {
		return nCoords;
	}
	
	public class CoordIterator {
		private int pos = 0;
		public int x, y, z;
		public int mode;
		public boolean hasNext() {
			return pos < dataSize;
		}
		public void next() {
			if(pos >= dataSize)
				throw new NoSuchElementException();
			x = data[pos++];
			y = data[pos++];
			z = data[pos++];
			mode = data[pos++]; // not currently used
		}
	}
	
	public CoordIterator iterate() {
		return new CoordIterator();
	}

	public void clear() {
		dataSize = 0;
	}
	
	public int getAllocatedSize() {
		return dataSize/4;
	}
}
