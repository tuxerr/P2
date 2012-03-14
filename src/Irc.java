import java.awt.Button;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Irc extends Frame {
	public TextArea text;
	public TextField data;
	SharedObject sentence;
	static String myName;

	public static void main(String argv[]) {

		if (argv.length != 1) {
			System.out.println("java Irc <name>");
			return;
		}
		myName = argv[0];

		// initialize the system
		Client.init();

		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		SharedObject s = Client.lookup("IRC");
		if (s == null) {
			s = Client.create(new Sentence());
			Client.register("IRC", s);
		}

		// create the graphical part
		new Irc(s);
	}

	public Irc(SharedObject s) {

		setLayout(new FlowLayout());

		text = new TextArea(10, 60);
		text.setEditable(false);
		text.setForeground(Color.red);
		add(text);

		data = new TextField(60);
		add(data);

		Button write_button = new Button("write");
		write_button.addActionListener(new writeListener(this));
		add(write_button);
		Button read_button = new Button("read");
		read_button.addActionListener(new readListener(this));
		add(read_button);

		setSize(470, 300);
		text.setBackground(Color.black);
		show();

		sentence = s;
	}
}

class readListener implements ActionListener {
	Irc irc;

	public readListener(Irc i) {
		irc = i;
	}

	public void actionPerformed(ActionEvent e) {

		System.out.println("QSGSFHDGFJGKHLBMNJLHKGFDYRSTHYJDHGYKHUL");
		// lock the object in read mode
		irc.sentence.lock_read();
		// invoke the method
		System.out.print(irc.sentence != null);
		System.out.print(irc.sentence.obj != null);
		String s = ((Sentence) (irc.sentence.obj)).read();
		// unlock the object
		irc.sentence.unlock();
		// display the read value
		irc.text.append(s + "\n");
	}
}

class writeListener implements ActionListener {
	Irc irc;

	public writeListener(Irc i) {
		irc = i;
	}

	public void actionPerformed(ActionEvent e) {

		// get the value to be written from the buffer
		String s = irc.data.getText();

		// lock the object in write mode
		irc.sentence.lock_write();

		// invoke the method
		((Sentence) (irc.sentence.obj)).write(Irc.myName + " wrote " + s);
		irc.data.setText("");

		// unlock the object
		irc.sentence.unlock();
	}
}
