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

import KBUtil.functional.DoubleToString;
import UI.exceptions.WindowStateException;
import gamedata.Champion;
import gamedata.EntityAnimation;
import gamedata.GameData;

import java.awt.BorderLayout;
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
	private TwilTextField tfCurrentFrame;
	private TwilTextField tfCurrentZoom;
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
	private JPanel animation_controls;

    public Window(){
        super("Le Test has Arrived");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 675, 441);

		//============ Content ==================
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
		
		animation_controls = new JPanel();
		contentPane.add(animation_controls, BorderLayout.EAST);
		animation_controls.setLayout(new BoxLayout(animation_controls, BoxLayout.Y_AXIS));
		
		JPanel anim_prop_controls = new JPanel();
		anim_prop_controls.setBorder(new TitledBorder(null, "Animation properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		animation_controls.add(anim_prop_controls);
		anim_prop_controls.setLayout(new BoxLayout(anim_prop_controls, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		panel.setToolTipText("An integer value will be the total number of cycles the animation takes.  \r\nA real number < 1 will be a multiplier (0.5 -> 2 cycles per frame).  \r\nA real number > 1 is invalid. I haven't enforced that yet please just don't use these values");
		anim_prop_controls.add(panel);
		
		dummyLabel = new JLabel("Speed");
		panel.add(dummyLabel);
		
		tfAnimSpeed = new JTextField();
		panel.add(tfAnimSpeed);
		tfAnimSpeed.setColumns(10);
		
		JPanel frame_controls = new JPanel();
		frame_controls.setBorder(new TitledBorder(null, "Frame properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		animation_controls.add(frame_controls);
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
		
		CardPanel element_controls = new CardPanel();
		animation_controls.add(element_controls);
		
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

		JPanel blank_card = new JPanel();
		element_controls.add(blank_card, "blank");
		
		element_controls.show("hitbox");

		JPanel blank_space = new JPanel();
		animation_controls.add(blank_space);
		blank_space.setLayout(new SpringLayout());

		JPanel Current_frame_controls = new JPanel();
		FlowLayout fl_Current_frame_controls = (FlowLayout) Current_frame_controls.getLayout();
		fl_Current_frame_controls.setAlignment(FlowLayout.LEFT);
		contentPane.add(Current_frame_controls, BorderLayout.SOUTH);
		
		JButton btnButtonLeft = new JButton("<-");
		Current_frame_controls.add(btnButtonLeft);
		
		tfCurrentFrame = new TwilTextField(2);
		Current_frame_controls.add(tfCurrentFrame);
		tfCurrentFrame.setEditable(false);
		
		JButton btnButtonRight = new JButton("->");
		Current_frame_controls.add(btnButtonRight);
		
		JButton btnzoomout = new JButton("-");
		Current_frame_controls.add(btnzoomout);
		
		tfCurrentZoom = new TwilTextField(4);
		Current_frame_controls.add(tfCurrentZoom);
		tfCurrentZoom.setEditable(false);
		
		JButton btnzoomin = new JButton("+");
		Current_frame_controls.add(btnzoomin);

		JMenuBar menu_bar = new JMenuBar();

		animations_menu = new JMenu("Animations");
		menu_bar.add(animations_menu);

		//============= CALLBACKS =============

		btnButtonRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EntityAnimationEditor displayer;
				try {
					displayer = getEADisplayer();
					displayer.incrFrame();
					repaint();
					updateCurrentFrameField(displayer);
				} catch (WindowStateException ex){
				}
			}
		});

		btnButtonLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EntityAnimationEditor displayer;
				try {
					displayer = getEADisplayer();
					displayer.decrFrame();
					repaint();
					updateCurrentFrameField(displayer);
				} catch (WindowStateException ex){
				}
			}
		});

		btnzoomout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				EntityAnimationEditor displayer;
				try {
					displayer = getEADisplayer();
					double zoom = displayer.getZoom();
					if (zoom > 1.0){
						zoom -= 1.0;
						displayer.setZoom(zoom);
						repaint();
						updateCurrentZoomField(displayer);
					}
				} catch (WindowStateException ex){
				}
			}
		});

		btnzoomin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				EntityAnimationEditor displayer;
				try {
					displayer = getEADisplayer();
					double zoom = displayer.getZoom();
					if (zoom < 4.0){
						zoom += 1.0;
						displayer.setZoom(zoom);
						repaint();
						updateCurrentZoomField(displayer);;
					}
				} catch (WindowStateException ex){
				}
			}
		});

		tfCurrentZoom.setDoubleTransform(new DoubleToString() {
			public String transform(double d){
				return Integer.toString((int)(d * 100)) + "%";
			}
		});

		//============= MENU ==================

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
		EntityAnimationEditor displayer = new EntityAnimationEditor(anim, this);
		displayCanvas.setDisplayable(displayer);
		updateCurrentFrameField(displayer);
		updateCurrentZoomField(displayer);
		repaint();
	}

	public EntityAnimationEditor getEADisplayer() throws WindowStateException {
		Displayable disp = displayCanvas.getDisplayable();
		if (disp instanceof EntityAnimationEditor){
			return (EntityAnimationEditor)disp;
		} else {
			throw new WindowStateException("User interacted with frame selector while displayed object was not an EntityAnimationEditor");
		}
	}

	private void updateCurrentFrameField(EntityAnimationEditor displayer){
		tfCurrentFrame.setText(displayer.getFrameIndex());
	}

	private void updateCurrentZoomField(EntityAnimationEditor displayer){
		tfCurrentZoom.setText(displayer.getZoom());
	}

	public void setDisplayedObject(Object o) throws UnsupportedOperationException{
		throw new UnsupportedOperationException();
	}

}
