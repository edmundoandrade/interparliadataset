package edworld.interparliadataset;

public class DocumentSentence {
	private int seq;
	private String sentence;

	public DocumentSentence(int seq, String sentence) {
		this.seq = seq;
		this.sentence = sentence;
	}

	public int getSeq() {
		return seq;
	}

	public String getSentence() {
		return sentence;
	}
}
