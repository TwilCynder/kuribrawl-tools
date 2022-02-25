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

import UI.exceptions.WindowStateException;
import gamedata.Champion;
import gamedata.EntityAnimation;
import gamedata.GameData;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Window extends JFrame{
	private Canvas displayCanvas;
    private JPanel contentPane;
	private JTextField tfAnimSpeed;
	private JTextField tfFrameDuration;
	private TwilSpinner spinFrameOriginX;
	private TwilSpinner spinFrameOriginY;
	private JTextField tfCurrentFrame;
	private JTextField tfCurrentZoom;
	private JTextField tfhitboxDamages;

	private JMenuBar menu_bar;
	private JMenu animations_menu;

	private GameData currentData = null;
	private JSpinner spinHurtboxX;
	private JSpinner spinHurtboxY;
	private JSpinner spinHurtboxWidth;
	private JSpinner spinHurtboxHeight;
	private JSpinner spinHitboxX;
	private JSpinner spinHitboxY;
	private JSpinner spinHitboxWidth;
	private JSpinner spinHitboxHeight;

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
		
		JPanel dummyPanel;
		JLabel dummyLabel;

		displayCanvas = new Canvas();
		contentPane.add(displayCanvas, BorderLayout.CENTER);
		
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
		
		dummyLabel = new JLabel("Speed");
		panel.add(dummyLabel);
		
		tfAnimSpeed = new JTextField();
		panel.add(tfAnimSpeed);
		tfAnimSpeed.setColumns(10);
		
		JPanel frame_controls = new JPanel();
		frame_controls.setBorder(new TitledBorder(null, "Frame properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		controls.add(frame_controls);
		frame_controls.setLayout(new BoxLayout(frame_controls, BoxLayout.Y_AXIS));
		
		dummyPanel = new JPanel();
		dummyPanel.setBorder(null);
		frame_controls.add(dummyPanel);
		
		dummyLabel = new JLabel("Duration");
		dummyPanel.add(dummyLabel);
		
		tfFrameDuration = new JTextField();
		dummyPanel.add(tfFrameDuration);
		tfFrameDuration.setColumns(10);
		
		dummyPanel = new JPanel();
		frame_controls.add(dummyPanel);
		
		dummyLabel = new JLabel("Origin");
		dummyPanel.add(dummyLabel);
		
		spinFrameOriginX = new TwilSpinner();
		dummyPanel.add(spinFrameOriginX);
		spinFrameOriginX.setColumns(2);

		spinFrameOriginY = new TwilSpinner();
		dummyPanel.add(spinFrameOriginY);
		spinFrameOriginY.setColumns(2);
		
		JPanel element_controls = new JPanel();
		controls.add(element_controls);
		element_controls.setLayout(new CardLayout(0, 0));
		
		JPanel hurtbox = new JPanel();
		hurtbox.setBorder(new TitledBorder(null, "Hurtbox properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		element_controls.add(hurtbox, "hurtbox");
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
		
		dummyLabel = new JLabel("X");
		hurtbox.add(dummyLabel, "2, 2, left, default");
		
		spinHurtboxX = new JSpinner();
		hurtbox.add(spinHurtboxX, "4, 2");
		
		dummyLabel = new JLabel("Y");
		hurtbox.add(dummyLabel, "6, 2, left, default");
		
		spinHurtboxY = new JSpinner();
		hurtbox.add(spinHurtboxY, "8, 2");
		
		dummyLabel = new JLabel("width");
		hurtbox.add(dummyLabel, "2, 4, left, default");
		
		spinHurtboxWidth = new JSpinner();
		hurtbox.add(spinHurtboxWidth, "4, 4");
		
		dummyLabel = new JLabel("height");
		hurtbox.add(dummyLabel, "6, 4, right, default");
		
		spinHurtboxHeight = new JSpinner();
		hurtbox.add(spinHurtboxHeight, "8, 4");
		
		String[] items = new String[] {"Normal", "Protected", "Invincible", "Intangible"};
		JComboBox<String> comboBox = new JComboBox<String>(items);
		hurtbox.add(comboBox, "2, 6, 7, 1, fill, default");
		
		JPanel hitbox = new JPanel();
		hitbox.setBorder(new TitledBorder(null, "Hitbox properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		element_controls.add(hitbox, "hitbox");
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
		
		dummyLabel = new JLabel("X");
		hitbox.add(dummyLabel, "2, 2, left, default");
		
		spinHitboxX = new JSpinner();
		hitbox.add(spinHitboxX, "4, 2");
		
		dummyLabel = new JLabel("Y");
		hitbox.add(dummyLabel, "6, 2, left, default");
		
		spinHitboxY = new JSpinner();
		hitbox.add(spinHitboxY, "8, 2");
		
		dummyLabel = new JLabel("width");
		hitbox.add(dummyLabel, "2, 4, left, default");
		
		spinHitboxWidth = new JSpinner();
		hitbox.add(spinHitboxWidth, "4, 4");
		
		dummyLabel = new JLabel("height");
		hitbox.add(dummyLabel, "6, 4, right, default");
		
		spinHitboxHeight = new JSpinner();
		hitbox.add(spinHitboxHeight, "8, 4");
		
		items = new String[] {"Normal", "Protected", "Invincible", "Intangible"};
		JComboBox<String> comboBox2 = new JComboBox<String>(items);
		hitbox.add(comboBox2, "2, 6, 7, 1, fill, default");
		
		dummyLabel = new JLabel("damages");
		hitbox.add(dummyLabel, "2, 8, right, default");
		
		tfhitboxDamages = new JTextField();
		tfhitboxDamages.setColumns(4);
		hitbox.add(tfhitboxDamages, "4, 8, fill, default");
		
		JPanel blank = new JPanel();
		element_controls.add(blank, "hitbox");
		
		JPanel blank_space = new JPanel();
		controls.add(blank_space);
		blank_space.setLayout(new SpringLayout());
		
		JPanel Current_frame_controls = new JPanel();
		FlowLayout fl_Current_frame_controls = (FlowLayout) Current_frame_controls.getLayout();
		fl_Current_frame_controls.setAlignment(FlowLayout.LEFT);
		contentPane.add(Current_frame_controls, BorderLayout.SOUTH);
		
		JButton btnButtonLeft = new JButton("<-");
		Current_frame_controls.add(btnButtonLeft);
		btnButtonLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EntityAnimationDisplayer displayer;
				try {
					displayer = getEADisplayer();
					displayer.incrFrame();
				} catch (WindowStateException ex){
					ex.printStackTrace();
				}
				repaint();
			}
		});
		
		tfCurrentFrame = new JTextField();
		Current_frame_controls.add(tfCurrentFrame);
		tfCurrentFrame.setColumns(2);
		tfCurrentFrame.setEditable(false);
		
		JButton btnButtonRight = new JButton("->");
		Current_frame_controls.add(btnButtonRight);
		
		JButton btnzoomout = new JButton("-");
		Current_frame_controls.add(btnzoomout);
		
		tfCurrentZoom = new JTextField();
		Current_frame_controls.add(tfCurrentZoom);
		tfCurrentZoom.setColumns(4);
		tfCurrentZoom.setEditable(false);
		
		JButton btnzoomin = new JButton("+");
		Current_frame_controls.add(btnzoomin);

		JMenuBar menu_bar = new JMenuBar();

		animations_menu = new JMenu("Animations");
		menu_bar.add(animations_menu);

		setJMenuBar(menu_bar);

        setMinimumSize(new Dimension(500, 370));
        setSize(350, 350);
        setLocationRelativeTo(null);
    }

	public void initAnimationsMenu(GameData gd){
		animations_menu.removeAll();

		for (Champion c : gd){
			JMenu champion_submenu = new JMenu(c.getDislayName());
            for (EntityAnimation anim : c){
				champion_submenu.add(new AnimationMenuItem(anim, this));
            }
			animations_menu.add(champion_submenu);
        }
	}

	public void setGameData(GameData gd){
		System.out.println("Using this GameData : ");
		for (Champion c : gd){
            for (EntityAnimation anim : c){
                System.out.println(anim.getName());
                //System.out.println(anim.getNbFrames());
                //System.out.println(anim.getSpeed());
            }
        }
		initAnimationsMenu(gd);
		currentData = gd;
	}

	public void setDisplayedObject(EntityAnimation anim){
		EntityAnimationDisplayer displayer = new EntityAnimationDisplayer(anim, 0);
		displayCanvas.setDisplayable(displayer);
		updateCurrentFrameField(displayer);
		updateCurrentZoomField(displayer);
		repaint();
	}

	public EntityAnimationDisplayer getEADisplayer() throws WindowStateException {
		Displayable disp = displayCanvas.getDisplayable();
		if (disp instanceof EntityAnimationDisplayer){
			return (EntityAnimationDisplayer)disp;
		} else {
			throw new WindowStateException("User interacted with frame selector while displayed object was not an EntityAnimationDisplayer");
		}
	}

	private void updateCurrentFrameField(EntityAnimationDisplayer displayer){
		tfCurrentFrame.setText(Integer.toString(displayer.getFrameIndex()));
	}

	private void updateCurrentZoomField(EntityAnimationDisplayer displayer){
		tfCurrentZoom.setText(Double.toString(displayer.getZoom()));
	}

	public void setDisplayedObject(Object o) throws UnsupportedOperationException{
		throw new UnsupportedOperationException();
	}

}
