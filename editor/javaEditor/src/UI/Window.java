package UI;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import KBUtil.functional.DoubleToString;
import UI.exceptions.WindowStateException;
import gamedata.Champion;
import gamedata.CollisionBox;
import gamedata.EntityAnimation;
import gamedata.EntityFrame;
import gamedata.Frame;
import gamedata.GameData;
import gamedata.Hitbox;
import gamedata.Hurtbox;
import gamedata.RessourcePath;
import gamedata.exceptions.GameDataException;
import gamedata.exceptions.InvalidRessourcePathException;
import gamedata.exceptions.TransparentGameDataException;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.Point;
import java.awt.CardLayout;

public class Window extends JFrame{

	private GameData currentData = null;
	private RessourcePath currentRessourcePath = null;
	private boolean modifsOccured = false;
	private List<String> currentFileList = null;
	
	private Canvas displayCanvas;
    private JPanel contentPane;
	private JTextField tfAnimSpeed;
	private JTextField tfFrameDuration;
	private IntegerSpinner spinFrameOriginX;
	private IntegerSpinner spinFrameOriginY;
	private TwilTextField tfCurrentFrame;
	private TwilTextField tfCurrentZoom;

	private IntegerSpinner spinHurtboxX;
	private IntegerSpinner spinHurtboxY;
	private IntegerSpinner spinHurtboxWidth;
	private IntegerSpinner spinHurtboxHeight;
	private IntegerSpinner spinHitboxX;
	private IntegerSpinner spinHitboxY;
	private IntegerSpinner spinHitboxWidth;
	private IntegerSpinner spinHitboxHeight;
	private JPanel animation_controls;
	private CardPanel element_controls;
	private JPanel hitbox_controls;
	private JPanel damage_hitbox;

	private JMenu animations_menu;

	private JTextField tfDamages;
	private JTextField tfAngle;
	private JTextField tfBKB;
	private JTextField tfSKB;
	private JSpinner spinHitID;
	private JComboBox comboAngleMode;
	private JPanel wind_hitbox;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JPanel blank;
	private JSpinner spinHitboxPrio;

	public  void errorPopup(String message){
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

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
		
		spinFrameOriginX = new IntegerSpinner();
		dummyPanel.add(spinFrameOriginX);
		spinFrameOriginX.setColumns(2);

		spinFrameOriginY = new IntegerSpinner();
		dummyPanel.add(spinFrameOriginY);
		spinFrameOriginY.setColumns(2);
		
		element_controls = new CardPanel();
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
		
		spinHurtboxX = new IntegerSpinner();
		hurtbox.add(spinHurtboxX, "4, 2");
		
		dummyLabel = new JLabel("Y");
		hurtbox.add(dummyLabel, "6, 2, left, default");
		
		spinHurtboxY = new IntegerSpinner();
		hurtbox.add(spinHurtboxY, "8, 2");
		
		dummyLabel = new JLabel("width");
		hurtbox.add(dummyLabel, "2, 4, left, default");
		
		spinHurtboxWidth = new IntegerSpinner();
		hurtbox.add(spinHurtboxWidth, "4, 4");
		
		dummyLabel = new JLabel("height");
		hurtbox.add(dummyLabel, "6, 4, right, default");
		
		spinHurtboxHeight = new IntegerSpinner();
		hurtbox.add(spinHurtboxHeight, "8, 4");
		
		//String[] items = new String[] {"Normal", "Protected", "Invincible", "Intangible"};
		JComboBox<String> comboBox = new JComboBox<String>(/*items*/);
		hurtbox.add(comboBox, "2, 6, 7, 1, fill, default");
		
		JPanel hitbox = new JPanel();
		hitbox.setBorder(new TitledBorder(null, "Hitbox properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		element_controls.add(hitbox, "hitbox");
		hitbox.setLayout(new FormLayout(new ColumnSpec[] {
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
				RowSpec.decode("default:grow"),}));
		
		dummyLabel = new JLabel("X");
		hitbox.add(dummyLabel, "2, 2, left, default");
		
		spinHitboxX = new IntegerSpinner();
		hitbox.add(spinHitboxX, "4, 2");
		
		dummyLabel = new JLabel("Y");
		hitbox.add(dummyLabel, "6, 2, left, default");
		
		spinHitboxY = new IntegerSpinner();
		hitbox.add(spinHitboxY, "8, 2");
		
		dummyLabel = new JLabel("width");
		hitbox.add(dummyLabel, "2, 4, left, default");
		
		spinHitboxWidth = new IntegerSpinner();
		hitbox.add(spinHitboxWidth, "4, 4");
		
		dummyLabel = new JLabel("height");
		hitbox.add(dummyLabel, "6, 4, right, default");
		
		spinHitboxHeight = new IntegerSpinner();
		hitbox.add(spinHitboxHeight, "8, 4");
		
		//items = new String[] {"Normal", "Protected", "Invincible", "Intangible"};
		JComboBox<String> comboHurboxType = new JComboBox<String>(/*items*/);
		hitbox.add(comboHurboxType, "2, 6, 7, 1, fill, default");
		
		hitbox_controls = new JPanel();
		hitbox.add(hitbox_controls, "2, 8, 7, 1, fill, fill");
		hitbox_controls.setLayout(new CardLayout(0, 0));
		
		damage_hitbox = new JPanel();
		hitbox_controls.add(damage_hitbox, "damage");
		damage_hitbox.setLayout(new FormLayout(new ColumnSpec[] {
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
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		dummyLabel = new JLabel("damages");
		damage_hitbox.add(dummyLabel, "2, 2, right, default");
		
		tfDamages = new JTextField();
		damage_hitbox.add(tfDamages, "4, 2, fill, default");
		tfDamages.setColumns(4);
		
		dummyLabel = new JLabel("angle");
		damage_hitbox.add(dummyLabel, "6, 2, right, default");
		
		tfAngle = new JTextField();
		damage_hitbox.add(tfAngle, "8, 2, fill, default");
		tfAngle.setColumns(3);
		
		dummyLabel = new JLabel("knockback :");
		damage_hitbox.add(dummyLabel, "2, 4, 5, 1");
		
		dummyLabel = new JLabel("base");
		damage_hitbox.add(dummyLabel, "2, 6, right, default");
		
		tfBKB = new JTextField();
		damage_hitbox.add(tfBKB, "4, 6, fill, default");
		tfBKB.setColumns(3);
		
		dummyLabel = new JLabel("scaling");
		damage_hitbox.add(dummyLabel, "6, 6, right, default");
		
		tfSKB = new JTextField();
		damage_hitbox.add(tfSKB, "8, 6, fill, default");
		tfSKB.setColumns(3);
		
		dummyLabel = new JLabel("hitID");
		damage_hitbox.add(dummyLabel, "2, 8, right, default");
		
		spinHitID = new JSpinner();
		damage_hitbox.add(spinHitID, "4, 8");
		
		dummyLabel = new JLabel("priority");
		damage_hitbox.add(dummyLabel, "6, 8, right, default");
		
		spinHitboxPrio = new JSpinner();
		damage_hitbox.add(spinHitboxPrio, "8, 8");
		
		dummyLabel = new JLabel("angle mode");
		damage_hitbox.add(dummyLabel, "2, 10, right, default");
		
		//items = new String[] {"Normal"};
		comboAngleMode = new JComboBox<String>(/**/);
		damage_hitbox.add(comboAngleMode, "4, 10, 5, 1, fill, default");
		
		wind_hitbox = new JPanel();
		hitbox_controls.add(wind_hitbox, "wind");
		wind_hitbox.setLayout(new FormLayout(new ColumnSpec[] {
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
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		dummyLabel = new JLabel("place");
		wind_hitbox.add(dummyLabel, "2, 2, right, default");
		
		textField_5 = new JTextField();
		wind_hitbox.add(textField_5, "4, 2, fill, default");
		textField_5.setColumns(4);
		
		dummyLabel = new JLabel("place");
		wind_hitbox.add(dummyLabel, "6, 2, right, default");
		
		textField_4 = new JTextField();
		wind_hitbox.add(textField_4, "8, 2, fill, default");
		textField_4.setColumns(4);
		
		dummyLabel = new JLabel("place");
		wind_hitbox.add(dummyLabel, "2, 4, right, default");
		
		textField_6 = new JTextField();
		wind_hitbox.add(textField_6, "4, 4, fill, default");
		textField_6.setColumns(4);
		
		dummyLabel = new JLabel("place");
		wind_hitbox.add(dummyLabel, "6, 4, right, default");
		
		textField_7 = new JTextField();
		wind_hitbox.add(textField_7, "8, 4, fill, default");
		textField_7.setColumns(4);
		
		blank = new JPanel();
		hitbox_controls.add(blank, "name_1186107637016000");

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

		tfAnimSpeed.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e){}
			public void focusLost(FocusEvent e){
				System.out.println("focus lost");
				double value; 
				try{
					value = Double.parseDouble(tfAnimSpeed.getText());
					EntityAnimationEditor editor = getEADisplayer();
					EntityAnimation anim = editor.getAnimation();
					anim.setSpeed(value);
					modifsOccured = true;
				} catch (NumberFormatException ex){
				}
			}
		});

		AbstractAction testAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				System.out.println("Test !");
			}
		};

		Action saveArchiveAction = new AbstractAction(){
			public void actionPerformed(ActionEvent e){
				if (currentRessourcePath == null){
					System.out.println("Can't save current ressource files as archive, as there is no open ressource folder");
					JOptionPane.showMessageDialog(Window.this, "Can't save current ressource files as archive, as there is no open ressource folder", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				if (modifsOccured()){
					switch (JOptionPane.showConfirmDialog(Window.this, 
						"This feature saves the ressource files to an archive in their current state.\nSome modifications to the game data have not been saved to the ressource files and will not be present in the archive.\nDo you want to save before archiving ?", "Editor", JOptionPane.YES_NO_CANCEL_OPTION))
					{
						case JOptionPane.YES_OPTION:
							//save
							break;
						case JOptionPane.NO_OPTION:
							break;
						default:
							return;
					}
				}
				
				PathChooser chooser = new PathChooser(PathChooser.Mode.FILE, currentRessourcePath.getPath());
				chooser.addFileFilters(new FileNameExtensionFilter("ZIP Archives", "zip"));
				chooser.setAcceptAllFileFilterUsed(false);
				Path dest = chooser.savePath(Window.this);

				if (dest != null){
					try {
						if (Files.exists(dest) && JOptionPane.showConfirmDialog(Window.this, 
						dest.toAbsolutePath().toString() + " already exists. Overwrite it ?", "Editor", JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION){
							return;
						}
						
						currentRessourcePath.saveAsArchive(currentFileList, dest);
					} catch (IOException ex){
						ex.printStackTrace();
						errorPopup("Could not save ressource directory as archive : \n" + ex.toString());
					}
				}
				
			}
		};

		Action saveAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e){

				saveData();
				
			}
		};

		Action saveAsAction = new AbstractAction() {
			public void actionPerformed(ActionEvent e){
				PathChooser chooser = new PathChooser(PathChooser.Mode.DIRECTORY, ".");
				Path selected = chooser.openPath(Window.this);
				if (selected == null) return;

				if (selected.equals(currentRessourcePath.getPath())) {
					saveData();
				}

				try {
					RessourcePath ressourcePath = new RessourcePath(selected);
					ressourcePath.copyUnmodifiedFiles(currentRessourcePath, currentData);
					saveDataTo(ressourcePath);
				} catch (InvalidRessourcePathException ex){
					ex.printStackTrace();
					errorPopup("Unable to open specified path : " + ex.getLocalizedMessage());
				} catch (IOException ex){
					ex.printStackTrace();
					errorPopup("Unable to copy files : " + ex.getLocalizedMessage());
				}
				
			}
		};

		//============= MENU ==================

		JMenuBar menu_bar = new JMenuBar();

		JMenu dummyMenu = new JMenu("File");

		JMenuItem dummyMenuItem = new JMenuItem("Test");
		dummyMenuItem.addActionListener(testAction);
		dummyMenu.add(dummyMenuItem);

		dummyMenuItem = new JMenuItem("Save");
		dummyMenuItem.addActionListener(saveAction);
		dummyMenu.add(dummyMenuItem);

		dummyMenuItem = new JMenuItem("Save As");
		dummyMenuItem.addActionListener(saveAsAction);
		dummyMenu.add(dummyMenuItem);

		dummyMenuItem = new JMenuItem("Save as Archive");
		dummyMenuItem.addActionListener(saveArchiveAction);
		dummyMenu.add(dummyMenuItem);

		menu_bar.add(dummyMenu);


		animations_menu = new JMenu("Animations");
		menu_bar.add(animations_menu);

		setJMenuBar(menu_bar);

        setMinimumSize(new Dimension(500, 400));
        setSize(500, 500);
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
		setGameData(gd, null);
	}

	public void setGameData(GameData gd, RessourcePath originPath){
		if (gd == null){
			throw new IllegalArgumentException("Passed null gamedata to Window.setGameData");
		}

		System.out.println("Using this GameData : ");
		for (Champion c : gd){
			System.out.println("==" + c.getDislayName() + "==");
            for (EntityAnimation anim : c){
                System.out.println(anim.getName());
                //System.out.println(anim.getNbFrames());
                //System.out.println(anim.getSpeed());
            }
        }
		initAnimationsMenu(gd);
		currentData = gd;
		currentRessourcePath = originPath;
		currentFileList = gd.getUsedFilenames();
		modifsOccured = false;
	}

	private void saveData(){
		if (currentRessourcePath == null){
			//TODO : utiliser le save-as
			return;
		}
		saveDataTo(currentRessourcePath);
	}

	private void saveDataTo(RessourcePath rPath){
		try {
			rPath.saveGameData(currentData);
		} catch (GameDataException ex){
			errorPopup("Error : invalid game data.");
			ex.printStackTrace();
		} catch (TransparentGameDataException ex) {
			errorPopup("Error : " + ex.getMessage());
			ex.printStackTrace();
		} catch (IOException ex){
			errorPopup("Error : file system error while writing file " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	public void setDisplayedObject(EntityAnimation anim){
		Interactable current = displayCanvas.getDisplayable();
		EntityAnimationEditor editor;
		if (current instanceof EntityAnimationEditor){
			editor = (EntityAnimationEditor)current;
			editor.setAnimation(anim);
		} else {
			editor = new EntityAnimationEditor(anim, this);
			displayCanvas.setDisplayable(editor);
		}
		updateCurrentFrameField(editor);
		updateCurrentZoomField(editor);
		repaint();
	}

	public EntityAnimationEditor getEADisplayer() throws WindowStateException {
		Displayable disp = displayCanvas.getDisplayable();
		if (disp == null) return null;
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

	public void updateAnimControls(EntityAnimation anim){
		tfAnimSpeed.setText(Double.toString(anim.getSpeed()));
	}

	public void updateFrameControls(Frame frame, EntityFrame entity_frame){
		tfFrameDuration.setText(Integer.toString(frame.getDuration()));
		Point origin = frame.getOrigin();
		spinFrameOriginX.setValue(origin.x);
		spinFrameOriginY.setValue(origin.y);
	}

	public void updateElementControls(CollisionBox cbox){
		if (cbox == null){
			element_controls.show("blank");
		}

		if (cbox instanceof Hurtbox){
			element_controls.show("hurtbox");
			Hurtbox hurtbox = (Hurtbox)cbox;
			spinHurtboxX.setValue(hurtbox.x);
			spinHurtboxY.setValue(hurtbox.y);
			spinHurtboxWidth.setValue(hurtbox.w);
			spinHurtboxHeight.setValue(hurtbox.h);
		}

		if (cbox instanceof Hitbox){
			element_controls.show("hitbox");
			Hurtbox hurtbox = (Hurtbox)cbox;
			spinHurtboxX.setValue(hurtbox.x); 
			spinHurtboxY.setValue(hurtbox.y);
			spinHurtboxWidth.setValue(hurtbox.w);
			spinHurtboxHeight.setValue(hurtbox.h);	
		}
	}

	private boolean modifsOccured(){
		return modifsOccured;
	}

	public void notifyGamedataModified(){
		modifsOccured = true;
	}
}
