package UI;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import gamedata.Champion;
import gamedata.EntityAnimation;
import gamedata.GameData;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class Window extends JFrame{
    private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_4;

	private GameData currentData;

    public Window(){
        super("Le Test has Arrived");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 675, 441);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		try {
            // Set System L&F
	        UIManager.setLookAndFeel(
	            UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       System.out.println("Cannot use system native Look&Feel (not supported), the window will be ugly asf");
	    }
        catch  (Exception e){
            System.out.println("Error while trying to set the Look&Feel, the window will be ugly asf");
        }
	    /*catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }*/
		
		JPanel canvas = new Canvas();
		contentPane.add(canvas, BorderLayout.CENTER);
		
		JPanel controls = new JPanel();
		contentPane.add(controls, BorderLayout.EAST);
		controls.setLayout(new BoxLayout(controls, BoxLayout.Y_AXIS));
		
		JPanel anim_controls = new JPanel();
		anim_controls.setBorder(new TitledBorder(null, "Animation properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		controls.add(anim_controls);
		anim_controls.setLayout(new BoxLayout(anim_controls, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		panel.setToolTipText("An integer value will be the total number of cycles the animation takes.  \r\nA real number < 1 will be a multiplier (0.5 -> 2 cycles per frame).  \r\nA real number > 1 is invalid. I haven't enforced that yet please just don't use these values");
		anim_controls.add(panel);
		
		JLabel lblNewLabel = new JLabel("Speed");
		panel.add(lblNewLabel);
		
		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(10);
		
		JPanel frame_controls = new JPanel();
		frame_controls.setBorder(new TitledBorder(null, "Frame properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		controls.add(frame_controls);
		frame_controls.setLayout(new BoxLayout(frame_controls, BoxLayout.Y_AXIS));
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(null);
		frame_controls.add(panel_1);
		
		JLabel lblNewLabel_1 = new JLabel("Duration");
		panel_1.add(lblNewLabel_1);
		
		textField_1 = new JTextField();
		panel_1.add(textField_1);
		textField_1.setColumns(10);
		
		JPanel panel_3 = new JPanel();
		frame_controls.add(panel_3);
		
		JLabel lblNewLabel_2 = new JLabel("Origin");
		panel_3.add(lblNewLabel_2);
		
		textField_2 = new JTextField();
		panel_3.add(textField_2);
		textField_2.setColumns(4);
		
		textField_3 = new JTextField();
		panel_3.add(textField_3);
		textField_3.setColumns(4);
		
		JPanel element_controls = new JPanel();
		controls.add(element_controls);
		element_controls.setLayout(new CardLayout(0, 0));
		
		JPanel hurtbox = new JPanel();
		hurtbox.setBorder(new TitledBorder(null, "Hurtbox properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		element_controls.add(hurtbox, "name_67627993851399");
		hurtbox.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblNewLabel_3 = new JLabel("X");
		hurtbox.add(lblNewLabel_3, "2, 2, left, default");
		
		JSpinner spinner = new JSpinner();
		hurtbox.add(spinner, "4, 2");
		
		JLabel lblNewLabel_4 = new JLabel("Y");
		hurtbox.add(lblNewLabel_4, "6, 2, left, default");
		
		JSpinner spinner_2 = new JSpinner();
		hurtbox.add(spinner_2, "8, 2");
		
		JLabel lblWidth = new JLabel("width");
		hurtbox.add(lblWidth, "2, 4, left, default");
		
		JSpinner spinner_1 = new JSpinner();
		hurtbox.add(spinner_1, "4, 4");
		
		JLabel lblHeight = new JLabel("height");
		hurtbox.add(lblHeight, "6, 4, right, default");
		
		JSpinner spinner_3 = new JSpinner();
		hurtbox.add(spinner_3, "8, 4");
		
		String[] items = new String[] {"Normal", "Protected", "Invincible", "Intangible"};
		JComboBox<String> comboBox = new JComboBox<String>(items);
		hurtbox.add(comboBox, "2, 6, 7, 1, fill, default");
		
		JPanel hitbox = new JPanel();
		hitbox.setBorder(new TitledBorder(null, "Hitbox properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		element_controls.add(hitbox, "Hitbox");
		hitbox.setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblNewLabel_3_ = new JLabel("X");
		hitbox.add(lblNewLabel_3_, "2, 2, left, default");
		
		JSpinner spinner_ = new JSpinner();
		hitbox.add(spinner_, "4, 2");
		
		JLabel lblNewLabel_4_ = new JLabel("Y");
		hitbox.add(lblNewLabel_4_, "6, 2, left, default");
		
		JSpinner spinner_2_ = new JSpinner();
		hitbox.add(spinner_2_, "8, 2");
		
		JLabel lblWidth_ = new JLabel("width");
		hitbox.add(lblWidth_, "2, 4, left, default");
		
		JSpinner spinner_1_ = new JSpinner();
		hitbox.add(spinner_1_, "4, 4");
		
		JLabel lblHeight_ = new JLabel("height");
		hitbox.add(lblHeight_, "6, 4, right, default");
		
		JSpinner spinner_3_ = new JSpinner();
		hitbox.add(spinner_3_, "8, 4");
		
		items = new String[] {"Normal", "Protected", "Invincible", "Intangible"};
		JComboBox<String> comboBox2 = new JComboBox<String>(items);
		hitbox.add(comboBox2, "2, 6, 7, 1, fill, default");
		
		JLabel lblNewLabel_5 = new JLabel("damages");
		hitbox.add(lblNewLabel_5, "2, 8, right, default");
		
		textField_4 = new JTextField();
		textField_4.setColumns(4);
		hitbox.add(textField_4, "4, 8, fill, default");
		
		JPanel blank = new JPanel();
		element_controls.add(blank, "name_58944544055700");
		
		JPanel blank_space = new JPanel();
		controls.add(blank_space);
		blank_space.setLayout(new SpringLayout());
		
		JPanel Current_frame_controls = new JPanel();
		FlowLayout fl_Current_frame_controls = (FlowLayout) Current_frame_controls.getLayout();
		fl_Current_frame_controls.setAlignment(FlowLayout.LEFT);
		contentPane.add(Current_frame_controls, BorderLayout.SOUTH);
		
		JButton btnButtonLeft = new JButton("<-");
		Current_frame_controls.add(btnButtonLeft);
		
		textField_5 = new JTextField();
		Current_frame_controls.add(textField_5);
		textField_5.setColumns(2);
		
		JButton btnButtonRight = new JButton("->");
		Current_frame_controls.add(btnButtonRight);
		
		JButton btnzoomout = new JButton("-");
		Current_frame_controls.add(btnzoomout);
		
		textField_6 = new JTextField();
		Current_frame_controls.add(textField_6);
		textField_6.setColumns(4);
		
		JButton btnzoomin = new JButton("+");
		Current_frame_controls.add(btnzoomin);

		JMenuBar menubar = new JMenuBar();

		JMenu animations_menu = new JMenu("Animations");
		menubar.add(animations_menu);

		setJMenuBar(menubar);

        setMinimumSize(new Dimension(500, 370));
        setSize(350, 350);
        setLocationRelativeTo(null);
    }

	public void setGameData(GameData gd){
		for (Champion c : gd){
            for (EntityAnimation anim : c){
                System.out.println(anim.getName());
                System.out.println(anim.getNbFrames());
                System.out.println(anim.getSpeed());
            }
        }
	}
}
