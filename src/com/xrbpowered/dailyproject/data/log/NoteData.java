package com.xrbpowered.dailyproject.data.log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.xrbpowered.dailyproject.data.InvalidFormatException;
import com.xrbpowered.dailyproject.data.XmlUtils;

public class NoteData implements Iterable<Note> {

	public static final String NOTE_DATA_PATH = "notes.xml";

	protected boolean saved = true;

	private final HashMap<String, Note> map = new HashMap<>();
	private final ArrayList<Note> notes = new ArrayList<>();

	public NoteData() {
		this(NOTE_DATA_PATH);
	}

	public NoteData(String path) {
		try {
			Element root = XmlUtils.loadXml(path);
			if(!root.getNodeName().equals("dailynotes")) {
				throw new InvalidFormatException();
			}
			
			NodeList noteNodes = root.getElementsByTagName("note");
			for(int i=0; i<noteNodes.getLength(); i++) {
				Note note = new Note((Element) noteNodes.item(i));
				notes.add(note);
				map.put(note.getKey(), note);
			}
			notes.sort(null);
		}
		catch(Exception e) {
			notes.clear();
			map.clear();
		}
	}
	
	public boolean save() {
		if(saved)
			return true;
		
		try {
			Document doc = XmlUtils.createDocument();
			Element root = doc.createElement("dailynotes");
			for(Note note : notes) {
				root.appendChild(note.toXml(doc));
			}
			doc.appendChild(root);
			XmlUtils.saveDocument(doc, NOTE_DATA_PATH);
			
			System.out.println("Notes saved");
			saved = true;
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
	
	@Override
	public Iterator<Note> iterator() {
		return notes.iterator();
	}
	
	public boolean remove(Note note) {
		if(notes.remove(note)) {
			map.remove(note.getKey());
			saved = false;
			return true;
		}
		else {
			return false;
		}
	}
	
	public void update(Note note) {
		if(note!=map.get(note.getKey())) {
			add(note);
		}
		saved = false;
	}
	
	public void add(Note note) {
		notes.remove(note);
		String key = note.getKey();
		if(map.containsKey(key))
			remove(map.get(key));
		map.put(key, note);
		notes.add(note);
		notes.sort(null);
		saved = false;
	}

	public Note getNoteAt(Calendar c, int col) {
		return map.get(Note.makeKey(c, col));
	}

	public Note getNoteAt(Calendar c, int row, int col) {
		if(row!=0) {
			c = (Calendar) c.clone();
			c.add(Calendar.DAY_OF_MONTH, row);
		}
		return getNoteAt(c, col);
	}
	
	public Note findNoteAfter(Note note) {
		for(Note n : notes) {
			if(n.compareTo(note)>0)
				return n;
		}
		return null;
	}
	
}
