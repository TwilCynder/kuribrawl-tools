//TODO : separate the editor and the window (a window class and an editor, the editor probably has a window property)

package UI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.Timer;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import KBUtil.functional.DoubleToString;
import KBUtil.ui.CardPanel;
import KBUtil.ui.IntegerSpinner;
import KBUtil.ui.MapComboBox;
import KBUtil.ui.MapComboBoxItem;
import KBUtil.ui.PathChooser;
import KBUtil.ui.PositiveSpinnerModel;
import KBUtil.ui.TwilTextField;
import KBUtil.ui.display.Canvas;
import KBUtil.ui.display.Displayable;
import KBUtil.ui.display.InteractableDisplayable;
import KBUtil.ui.documentFilters.IntegerDocumentFilter;
import KBUtil.ui.documentFilters.RealNumberDocumentFilter;
import UI.displayers.AbstractAnimationEditorBackend;
import UI.displayers.AbstractEntityAnimationEditorBackend;
import UI.displayers.AnimationDisplayer;
import UI.displayers.AnimationEditor;
import UI.displayers.EditorFrontend;
import UI.displayers.EntityAnimationDisplayer;
import UI.displayers.EntityAnimationEditor;
import UI.exceptions.WindowStateException;
import UI.forms.ChangeDescriptorFilenameForm;
import UI.forms.ChangeSourceImageForm;
import UI.forms.NewAnimationForm;
import UI.forms.RenameChampionDescriptorForm;
import UI.forms.RenameSourceImageForm;
import gamedata.AngleMode;
import gamedata.Animation;
import gamedata.Champion;
import gamedata.CollisionBox;
import gamedata.DamageHitbox;
import gamedata.EntityAnimation;
import gamedata.EntityFrame;
import gamedata.Frame;
import gamedata.GameData;
import gamedata.Hitbox;
import gamedata.Hurtbox;
import gamedata.HurtboxType;
import gamedata.NamedAnimationPool;
import gamedata.RessourcePath;
import gamedata.Stage;
import gamedata.WindHitbox;
import gamedata.RessourcePath.MissingInfoListener;
import gamedata.exceptions.GameDataException;
import gamedata.exceptions.InvalidRessourcePathException;
import gamedata.exceptions.RessourceException;
import gamedata.exceptions.TransparentGameDataException;

public class Window extends JFrame implements EntityAnimationEditorWindow {

	private GameData currentData = null;
	private RessourcePath currentRessourcePath = null;
	private boolean modifsOccured = false;
	private boolean initializing = true;
	private List<String> currentFileList = null;

	private Canvas displayCanvas;
    private JPanel contentPane;
	private TwilTextField tfAnimSpeed;
	private TwilTextField tfFrameDuration;
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
	private CardPanel hitbox_typespecific_controls;
	private JPanel damage_hitbox;
	private JPanel wind_hitbox;

	private JMenuBar preload_bar;	
	private JMenuBar gamedata_bar;

	private JMenu animations_menu;
	private JMenu gameDataMenu;

	Collection<JMenuItem> baseGamedataMenuItems = new LinkedList<>();
	Collection<JMenuItem> animationGamedataMenuItems = new LinkedList<>();
	Collection<JMenuItem> championGamedataMenuItems = new LinkedList<>();

	private JTextField tfDamages;
	private TwilTextField tfAngle;
	private JTextField tfBKB;
	private JTextField tfSKB;
	private IntegerSpinner spinHitID;
	private IntegerSpinner spinHitboxPrio;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JPanel blank;
	private CardPanel editor_controls;

	private static final String baseTitle = "Kuribrawl GameData Editor";

	private static Map <HurtboxType, String> hurtboxTypesNames = new EnumMap<>(HurtboxType.class){{
		put(HurtboxType.NORMAL, "Normal");
		put(HurtboxType.INTANGIBLE, "Intangible");
		put(HurtboxType.PROTECTED, "Protected");
		put(HurtboxType.INVINCIBLE, "Invincible");
	}};

	private enum HitboxType {
		DAMAGE("Damaging", DamageHitbox.class),
		WIND("Windbox", WindHitbox.class);

		private String name;
		private Class<? extends Hitbox> hitboxClass;

		private HitboxType(String name, Class<? extends Hitbox> hitboxClass){
			this.name = name;
			this.hitboxClass = hitboxClass;
		}

		@Override
		public String toString(){
			return name;
		}

		public Class<? extends Hitbox> getHitboxClass(){

			return hitboxClass;
		}
	}

	private static Map <AngleMode, String> angleModeNames = new EnumMap<>(AngleMode.class){{
		put(AngleMode.NORMAL, "Normal");
	}};

	private static Map<Class<? extends Hitbox>, HitboxType> hitboxTypes = new TreeMap<>(new Comparator<Class<? extends Hitbox>>() {
		@Override
		public int compare(Class<? extends Hitbox> left, Class<? extends Hitbox> right){
			return left.getName().compareTo(right.getName());
		}
	})
	{{
		for(HitboxType type : HitboxType.values()){
			put(type.getHitboxClass(), type);
		}
	}};

	private MapComboBox<HurtboxType, String> comboHurtboxType;
	private JComboBox<HitboxType> comboHitboxType;
	private MapComboBox<AngleMode, String> comboAngleMode;

	public  void errorPopup(String message){
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}

	/**
	 * Change listener made for IntegerSpinner's in the editor.
	 * stateChanged(EntityAnimationEditor, int) will be called on focus lost
	 */
	private abstract class SpinChangeListener implements ChangeListener {
		public abstract void stateChanged(int value);

		public void stateChanged(ChangeEvent e){
			if (!(e.getSource() instanceof IntegerSpinner)) throw new IllegalStateException("IntegerSpinner-specific change listener called on another component");
			IntegerSpinner source = (IntegerSpinner)e.getSource();
			try{
				int value = source.getValueInt();
				stateChanged(value);
				updateVisualEditor();
			} catch (WindowStateException ex){
				if (!initializing) throw ex;
			}
		}
	}

	/**
	 * Change listener made for TwilTextField's in the editor.
	 * focusLost(EntityAnimationEditor, TwiltextField) will be called on focus lost,
	 * any NumberFormatException raised by your exception will be handled.
	 */
	private abstract class TwilFocusListener<T extends JComponent> implements FocusListener {
		Class <T> componentClass;
		public TwilFocusListener(Class<T> componentType){
			componentClass = componentType;
		}

		public abstract void focusLost(T source) throws NumberFormatException;

		public void focusGained(FocusEvent e){}

		@SuppressWarnings("unchecked")
		public void focusLost(FocusEvent e){
			if (!componentClass.isInstance(e.getSource())) throw new IllegalStateException("Typed listener specific to " + componentClass.getName() + "used on different component : " + e.getSource());
			T source = (T)e.getSource(); //do not worry
			try{
				focusLost(source);  
			} catch (NumberFormatException ex){
				System.out.println("Garbage input in tf frame duration");
			}
		}
	}

	private Action loadGameDataAction = new AbstractAction("Load Game Data") {
		public void actionPerformed(ActionEvent e){
			openResourcePathDialogue();
		}
	};

	private Action closeGameDataAction = new AbstractAction("Close Game Data") {
		public void actionPerformed(ActionEvent e){
			if (checkBeforeClosing()){ //check if we can close safely
				closeGameData();
			}
		}
	};

	private Action saveAsAction = new AbstractAction("Save as") {
		public void actionPerformed(ActionEvent e){
			saveDataAs();
		}
	};

	private Action saveAction = new AbstractAction("Save") {
		public void actionPerformed(ActionEvent e){
			if (modifsOccured()){
				saveData();
			}
		}
	};

	
	private Action testAction = new AbstractAction("Test"){
		public void actionPerformed(ActionEvent e){
			System.out.println("Test !");
		}
	};

	private Action saveArchiveAction = new AbstractAction("Save as archive"){
		public void actionPerformed(ActionEvent e){
			if (currentRessourcePath == null){
				System.out.println("Can't save current ressource files as archive, as there is no open ressource folder");
				errorPopup("Can't save current ressource files as archive, as there is no open ressource folder");
				return;
			}

			if (modifsOccured()){
				switch (JOptionPane.showConfirmDialog(Window.this,
					"This feature saves the ressource files to an archive in their current state.\nSome modifications to the game data have not been saved to the ressource files and will not be present in the archive.\nDo you want to save before archiving ?", "Editor", JOptionPane.YES_NO_CANCEL_OPTION))
				{
					case JOptionPane.YES_OPTION:
						saveData();
						break;
					case JOptionPane.NO_OPTION:
						break;
					default:
						return;
				}
			}

			PathChooser chooser = new PathChooser(PathChooser.Mode.FILE, currentRessourcePath.getPath());
			chooser.addChoosableFileFilters(new FileNameExtensionFilter("ZIP Archives", "zip"));
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

	Action newAnimationAction = new AbstractAction("New animation") {
		public void actionPerformed(ActionEvent e){
			if (currentData == null) return;
			if (currentRessourcePath == null) {
				errorPopup("Cannot create a new animation without a ressource path to get files from.");
				return;
			}

			new NewAnimationForm(Window.this).showForm();
		}
	};

	Action changeDescriptorAction = new AbstractAction("Change animation descriptor filename") {
		@Override
		public void actionPerformed(ActionEvent e) {
			setCurrentAnimDescriptor();
		}
	};

	Action renameSourceImageAction = new AbstractAction("Rename animation source image") {
		@Override
		public void actionPerformed(ActionEvent e) {
			renameCurrentAnimSourceImage();
		}
	};

	Action changeSourceImageAction = new AbstractAction("Change animation source image") {
		@Override
		public void actionPerformed(ActionEvent e) {
			changeCurrentAnimSourceImage();
		}
	};

	Action renameChampionDescriptorAction = new AbstractAction("Rename champion descriptor") {
		@Override
		public void actionPerformed(ActionEvent e) {
			renamecurrentChampionDescriptor();
		}
	};

	private void createLayout(){
		JPanel dummyPanel;
		JLabel dummyLabel;

		displayCanvas = new Canvas();
		contentPane.add(displayCanvas, BorderLayout.CENTER);

		editor_controls = new CardPanel();
		contentPane.add(editor_controls, BorderLayout.EAST);

		animation_controls = new JPanel();
		editor_controls.add(animation_controls, "EntityAnimation");
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

		tfAnimSpeed = new TwilTextField();
		panel.add(tfAnimSpeed);
		tfAnimSpeed.setColumns(10);
		tfAnimSpeed.setDocumentFilter(RealNumberDocumentFilter.staticInstance);

		JPanel frame_controls = new JPanel();
		frame_controls.setBorder(new TitledBorder(null, "Frame properties", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		animation_controls.add(frame_controls);
		frame_controls.setLayout(new BoxLayout(frame_controls, BoxLayout.Y_AXIS));

		dummyPanel = new JPanel();
		dummyPanel.setBorder(null);
		frame_controls.add(dummyPanel);

		dummyLabel = new JLabel("Duration");
		dummyPanel.add(dummyLabel);

		tfFrameDuration = new TwilTextField();
		dummyPanel.add(tfFrameDuration);
		tfFrameDuration.setColumns(10);
		tfFrameDuration.setDocumentFilter(IntegerDocumentFilter.staticInstance);

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
		spinHurtboxWidth.setModel(new PositiveSpinnerModel());
		hurtbox.add(spinHurtboxWidth, "4, 4");

		dummyLabel = new JLabel("height");
		hurtbox.add(dummyLabel, "6, 4, right, default");

		spinHurtboxHeight = new IntegerSpinner();
		spinHurtboxHeight.setModel(new PositiveSpinnerModel());
		hurtbox.add(spinHurtboxHeight, "8, 4");

		comboHurtboxType = new MapComboBox<>(hurtboxTypesNames);
		hurtbox.add(comboHurtboxType, "2, 6, 7, 1, fill, default");

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
		spinHitboxWidth.setModel(new PositiveSpinnerModel());
		hitbox.add(spinHitboxWidth, "4, 4");

		dummyLabel = new JLabel("height");
		hitbox.add(dummyLabel, "6, 4, right, default");

		spinHitboxHeight = new IntegerSpinner();
		spinHitboxHeight.setModel(new PositiveSpinnerModel());
		hitbox.add(spinHitboxHeight, "8, 4");

		comboHitboxType = new JComboBox<HitboxType>(HitboxType.values());
		hitbox.add(comboHitboxType, "2, 6, 7, 1, fill, default");

		hitbox_typespecific_controls = new CardPanel();
		hitbox.add(hitbox_typespecific_controls, "2, 8, 7, 1, fill, fill");

		damage_hitbox = new JPanel();
		hitbox_typespecific_controls.add(damage_hitbox, HitboxType.DAMAGE.toString());
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

		tfAngle = new TwilTextField();
		damage_hitbox.add(tfAngle, "8, 2, fill, default");
		tfAngle.setColumns(3);
		tfAngle.setDocumentFilter(IntegerDocumentFilter.staticInstance);


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

		spinHitID = new IntegerSpinner();
		damage_hitbox.add(spinHitID, "4, 8");

		dummyLabel = new JLabel("priority");
		damage_hitbox.add(dummyLabel, "6, 8, right, default");

		spinHitboxPrio = new IntegerSpinner();
		damage_hitbox.add(spinHitboxPrio, "8, 8");

		dummyLabel = new JLabel("angle mode");
		damage_hitbox.add(dummyLabel, "2, 10, right, default");

		//items = new String[] {"Normal"};
		comboAngleMode = new MapComboBox<>(angleModeNames);
		damage_hitbox.add(comboAngleMode, "4, 10, 5, 1, fill, default");

		wind_hitbox = new JPanel();
		hitbox_typespecific_controls.add(wind_hitbox, HitboxType.WIND.toString());
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
		hitbox_typespecific_controls.add(blank, "name_1186107637016000");

		JPanel blank_card = new JPanel();
		element_controls.add(blank_card, "blank");

		element_controls.show("blank");

		JPanel blank_space = new JPanel();
		animation_controls.add(blank_space);
		blank_space.setLayout(new SpringLayout());

		dummyPanel = new JPanel();
		editor_controls.add(dummyPanel, "blank");

		editor_controls.show("blank");

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
				AnimationDisplayer displayer;
				try {
					displayer = getADisplayer();
					displayer.incrFrame();
					updateVisualEditor();
					updateCurrentFrameField(displayer);
				} catch (WindowStateException ex){
				}
			}
		});

		btnButtonLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AnimationDisplayer displayer;
				try {
					displayer = getADisplayer();
					displayer.decrFrame();
					updateVisualEditor();
					updateCurrentFrameField(displayer);
				} catch (WindowStateException ex){
				}
			}
		});

		btnzoomout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				AnimationDisplayer displayer;
				try {
					displayer = getADisplayer();
					double zoom = displayer.getZoom();
					if (zoom > 1.0){
						zoom -= 1.0;
						displayer.setZoom(zoom);
						updateVisualEditor();
						updateCurrentZoomField(displayer);
					}
				} catch (WindowStateException ex){
				}
			}
		});

		btnzoomin.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				AnimationDisplayer displayer;
				try {
					displayer = getADisplayer();
					double zoom = displayer.getZoom();
					if (zoom < 4.0){
						zoom += 1.0;
						displayer.setZoom(zoom);
						updateVisualEditor();
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

		tfAnimSpeed.addFocusListener(new TwilFocusListener<TwilTextField>(TwilTextField.class){
			public void focusLost(TwilTextField source){
				AnimationDisplayer editor = getADisplayer();
				Animation anim = editor.getAnimation();
				double value = Double.parseDouble(source.getText());
				anim.setSpeed(value);
				
				notifyDataModified();
			}
		});

		tfFrameDuration.addFocusListener(new TwilFocusListener<TwilTextField>(TwilTextField.class) {
			public void focusLost(TwilTextField source){
				int value = source.getInt();
				AnimationDisplayer editor = getADisplayer();
				Frame frame = editor.getCurrentFrame();
				frame.setDuration(value);

				notifyDataModified();
			}
		});

		spinFrameOriginX.addChangeListener(new SpinChangeListener() {
			public void stateChanged(int value){
				AbstractAnimationEditorBackend editor = getAEditorBackend();
				editor.moveOriginX(value);
				notifyDataModified();
			}
		});

		spinFrameOriginY.addChangeListener(new SpinChangeListener() {
			public void stateChanged(int value){
				AbstractAnimationEditorBackend editor = getAEditorBackend();
				editor.moveOriginY(value);
				notifyDataModified();
			}
		});

		ChangeListener cboxXChangeListener = new SpinChangeListener() {
			@Override
			public void stateChanged(int value) {
				EntityAnimationDisplayer editor = getEADisplayer();
				CollisionBox cbox = editor.getSelectedCBox();
				if (cbox != null)
					cbox.x = value;
					notifyDataModified();
			}
		};

		spinHurtboxX.addChangeListener(cboxXChangeListener);
		spinHitboxX.addChangeListener(cboxXChangeListener);

		ChangeListener cboxYChangeListener = new SpinChangeListener() {
			@Override
			public void stateChanged(int value) {
				EntityAnimationDisplayer editor = getEADisplayer();
				CollisionBox cbox = editor.getSelectedCBox();
				if (cbox != null)
					cbox.y = value;
					notifyDataModified();
			}
		};

		spinHurtboxY.addChangeListener(cboxYChangeListener);
		spinHitboxY.addChangeListener(cboxYChangeListener);

		ChangeListener cboxWChangeListener = new SpinChangeListener() {
			@Override
			public void stateChanged(int value) {
				EntityAnimationDisplayer editor = getEADisplayer();
				CollisionBox cbox = editor.getSelectedCBox();
				if (cbox != null)
					cbox.w = value > 0 ? value : 1;
					notifyDataModified();
			}
		};

		spinHurtboxWidth.addChangeListener(cboxWChangeListener);
		spinHitboxWidth.addChangeListener(cboxWChangeListener);

		ChangeListener cboxHChangeListener = new SpinChangeListener() {
			@Override
			public void stateChanged(int value) {
				EntityAnimationDisplayer editor = getEADisplayer();
				CollisionBox cbox = editor.getSelectedCBox();
				if (cbox != null)
					cbox.h = value;
					notifyDataModified();
			}
		};

		spinHurtboxHeight.addChangeListener(cboxHChangeListener);
		spinHitboxHeight.addChangeListener(cboxHChangeListener);

		comboHurtboxType.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				if (e.getStateChange() != ItemEvent.SELECTED) return;
				try {
					if (e.getItem() instanceof MapComboBoxItem){
						MapComboBoxItem<?, ?> item = (MapComboBoxItem<?, ?>)e.getItem();
						 if (item.getValue() instanceof HurtboxType){
							HurtboxType type = (HurtboxType)item.getValue();
							EntityAnimationDisplayer displayer = getEADisplayer();
							Hurtbox hurtbox = displayer.getSelectedHurtbox();

							hurtbox.type = type; //wooooo tout ça pour ça t content twil dmerd

							notifyDataModified();
						}
					}
				} catch (WindowStateException ex){
					ex.printStackTrace();
				}
			}
		});

		comboHitboxType.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				if (e.getStateChange() != ItemEvent.SELECTED) return;
				try {
					if (e.getItem() instanceof HitboxType){
						HitboxType type = (HitboxType)e.getItem();
						EntityAnimationDisplayer displayer = getEADisplayer();
						Hitbox hitbox = displayer.getSelectedHitbox();
						if (hitbox.getClass() != type.getHitboxClass()){
							EntityFrame frame;
							frame = displayer.getCurrentEntityFrame();
							Hitbox newHitbox = null;

							System.out.println("Selected item : " + type);
							switch (type){
								case DAMAGE:
									newHitbox = new DamageHitbox(hitbox);
									break;
								case WIND:
									newHitbox = new WindHitbox(hitbox);
							}
							int index = frame.hitboxes.indexOf(hitbox);
							if (index == -1) throw new IllegalStateException("Selected hitbox is not in the current frame hitboxes list");
							frame.hitboxes.set(index, newHitbox);
							getEADisplayer().setSelectedCBox(newHitbox);
							updateHitboxTypeSpecificControls(newHitbox, type, false);

							notifyDataModified();
						}
					}

				} catch (WindowStateException ex){
					ex.printStackTrace();
				}
			}
		});

		tfDamages.addFocusListener(new TwilFocusListener<JTextField>(JTextField.class) {
			@Override
			public void focusLost(JTextField source){
				EntityAnimationDisplayer editor = getEADisplayer();
				double value = Double.parseDouble(source.getText());
				DamageHitbox damage_hitbox = (DamageHitbox)editor.getSelectedDamageHitbox();
				damage_hitbox.damage = value;
				notifyDataModified();
			}
		});

		tfAngle.addFocusListener(new TwilFocusListener<TwilTextField>(TwilTextField.class) {
			@Override
			public void focusLost(TwilTextField source){
				EntityAnimationDisplayer editor = getEADisplayer();
				int value = source.getInt();
				DamageHitbox damage_hitbox = (DamageHitbox)editor.getSelectedDamageHitbox();
				damage_hitbox.angle = value;
				notifyDataModified();
			}
		});

		tfBKB.addFocusListener(new TwilFocusListener<JTextField>(JTextField.class) {
			@Override
			public void focusLost(JTextField source){
				EntityAnimationDisplayer editor = getEADisplayer();
				double value = Double.parseDouble(source.getText());
				DamageHitbox damage_hitbox = (DamageHitbox)editor.getSelectedDamageHitbox();
				damage_hitbox.base_knockback = value;
				notifyDataModified();
			}
		});

		tfSKB.addFocusListener(new TwilFocusListener<JTextField>(JTextField.class) {
			@Override
			public void focusLost(JTextField source){
				EntityAnimationDisplayer editor = getEADisplayer();
				double value = Double.parseDouble(source.getText());
				DamageHitbox damage_hitbox = (DamageHitbox)editor.getSelectedDamageHitbox();
				damage_hitbox.scaling_knockback = value;
				notifyDataModified();
			}
		});

		spinHitboxPrio.addChangeListener(new SpinChangeListener() {
			public void stateChanged(int value){
				EntityAnimationDisplayer editor = getEADisplayer();
				DamageHitbox damage_hitbox = (DamageHitbox)editor.getSelectedDamageHitbox();
				damage_hitbox.priority = value;
				notifyDataModified();
			}
		});

		spinHitID.addChangeListener(new SpinChangeListener() {
			public void stateChanged(int value){
				EntityAnimationDisplayer editor = getEADisplayer();
				DamageHitbox damage_hitbox = (DamageHitbox)editor.getSelectedDamageHitbox();
				damage_hitbox.hitID = value;
				notifyDataModified();
			}
		});

		comboAngleMode.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e){
				if (e.getStateChange() != ItemEvent.SELECTED) return;
				try {
					if (e.getItem() instanceof MapComboBoxItem){
						MapComboBoxItem<?, ?> item = (MapComboBoxItem<?, ?>)e.getItem();
						if (item.getValue() instanceof AngleMode){
							AngleMode type = (AngleMode)item.getValue();
							EntityAnimationDisplayer displayer = getEADisplayer();
							DamageHitbox damage_hitbox = (DamageHitbox)displayer.getSelectedDamageHitbox();

							damage_hitbox.angle_mode = type;

							notifyDataModified();
						}
					}
				} catch (WindowStateException ex){
					ex.printStackTrace();
				}
			}
		});

	}

    public Window(){
        super(baseTitle);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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

		createLayout();

		//============= MENU ==================

		initMenus();
		setMenuBar_(preload_bar);

		//=========== SHORTCUTS ==================

		/*getRootPane().getInputMap().put(KeyStroke.getKeyStroke("S"),"doSomething");
		getRootPane().getActionMap().put("doSomething",
				testAction);*/

		//========== WINDOW LISTENER =============

		

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				if (checkBeforeClosing()){ //check if we can close safely
					System.exit(0);
				}
			}
		});

		ignoreModifications();

		//=========== END INIT ===================

        setMinimumSize(new Dimension(500, 400));
        setSize(500, 500);
        setLocationRelativeTo(null);


		System.out.println("end init");
    }

	public GameData getCurrentData() {
		return currentData;
	}

	public RessourcePath getCurrentRessourcePath() {
		return currentRessourcePath;
	}

	private void ignoreModifications(){
		if (initializing) return;
		Timer timer = new Timer(20, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e){
				initializing = false;
			}
		});
		timer.setRepeats(false);
		timer.start();
	}

	private void setMenuBar_(JMenuBar bar){
		if (getJMenuBar() == bar) return;

		setJMenuBar(bar);
		SwingUtilities.updateComponentTreeUI(this);
	}

	private void addItemsToMenu(JMenu menu, Collection<JMenuItem> items) {
		System.out.println("AITM " + items.size());
		for (var item : items){
			if (item == null){
				menu.addSeparator();
			} else {
				menu.add(item);
			}	
		}
	}

	private JMenuItem createMenuItem(Action action, KeyStroke ks){
		JMenuItem item = new JMenuItem(action);
		item.setAccelerator(ks);
		return item;
	}

	private void initMenus(){
		preload_bar = new JMenuBar();
		gamedata_bar = new JMenuBar();

		JMenu dummyMenu;
		JMenuItem dummyMenuItem;

		// base file menu
		dummyMenu = new JMenu("File");

		dummyMenu.add(createMenuItem(loadGameDataAction, KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK)));
		dummyMenu.add(createMenuItem(testAction, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK)));

		preload_bar.add(dummyMenu);

		// file menu items with gd loaded
		dummyMenu = new JMenu("File");

		dummyMenu.add(createMenuItem(loadGameDataAction, KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK)));
		dummyMenu.add(createMenuItem(testAction, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK)));

		dummyMenu.addSeparator();

		dummyMenu.add(createMenuItem(closeGameDataAction, KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK)));
		dummyMenu.add(createMenuItem(saveAction, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)));
		dummyMenu.add(new JMenuItem(saveAsAction));
		dummyMenu.add(new JMenuItem(saveArchiveAction));

		gamedata_bar.add(dummyMenu);

		//animations menu
		animations_menu = new JMenu("Animations");
		gamedata_bar.add(animations_menu);

		//game data menu
		gameDataMenu = new JMenu("Game Data");

		dummyMenuItem = new JMenuItem(newAnimationAction);
		baseGamedataMenuItems.add(dummyMenuItem);
		dummyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));

		animationGamedataMenuItems.add(new JMenuItem(changeDescriptorAction));
		animationGamedataMenuItems.add(new JMenuItem(renameSourceImageAction));
		animationGamedataMenuItems.add(new JMenuItem(changeSourceImageAction));

		championGamedataMenuItems.add(new JMenuItem(renameChampionDescriptorAction));

		addItemsToMenu(gameDataMenu, baseGamedataMenuItems);
		gamedata_bar.add(gameDataMenu);
	}

	/**
	 * Sets the current items in the Game Data menu, with the content of one or more item collections. 
	 * Item collections will be separated by a standard separator in the menu. 
	 * @param items collections of items. 
	 */
	@SafeVarargs
	private void setGameDataMenuItems(Collection<JMenuItem>... items){
		gameDataMenu.removeAll();
		addItemsToMenu(gameDataMenu, baseGamedataMenuItems);
		if (items == null) return;
		for (Collection<JMenuItem> collection : items){
			gameDataMenu.addSeparator();
			addItemsToMenu(gameDataMenu, collection);
		}
	}



	private void initAnimationsMenu(GameData gd){
		animations_menu.removeAll();

		if (gd == null) return;

		for (Champion c : gd){
			JMenu champion_submenu = new JMenu(c.getDisplayName());
            for (EntityAnimation anim : c){
				champion_submenu.add(EntityAnimationMenuItem.create(anim, this));
            }
			animations_menu.add(champion_submenu);
		}

		for (Stage s : gd.getStages()){
			JMenu stages_submenu = new JMenu(s.getDisplayName());
			for (Animation anim : s){
				stages_submenu.add(AnimationMenuItem.create(anim, this));
            }
			animations_menu.add(stages_submenu);
		}
	}

	/**
	 * Call this when the animations list has been modified (animation added, removed or renamed)
	 */
	public void updateAnimations(){
		initAnimationsMenu(currentData);
	}

	public void notifyDataModified(){
		if (initializing) return;
		System.out.println("NOTIFY");
		if (!modifsOccured){
			setTitle(baseTitle + " | (modified) " + currentRessourcePath.getPath());
			modifsOccured = true;
		}

	}

	public void resetDataModified(){
		System.out.println("RESET");
		if (modifsOccured){
			setTitle(baseTitle + " | " + currentRessourcePath.getPath());
			modifsOccured = false;
		}
	}

	public void setGameData(GameData gd){
		setGameData(gd, null);
	}

	public void setGameData(GameData gd, RessourcePath originPath){
		setTitle(baseTitle + " | " + originPath.getPath());

		if (gd == null){
			throw new IllegalArgumentException("Passed null gamedata to Window.setGameData");
		}

		
		initAnimationsMenu(gd);
		currentData = gd;
		currentRessourcePath = originPath;
		currentFileList = gd.getUsedFilenames();
		resetDataModified();
		setMenuBar_(gamedata_bar);
	} 

	public void openResourcePathDialogue(){
		PathChooser chooser = new PathChooser(PathChooser.Mode.DIRECTORY, ".");
		Path selected = chooser.openPath(this);

		if (selected != null){
			GameData gd;
			try {
				RessourcePath originPath = new RessourcePath(selected);

				if (currentData != null){
					if (!checkBeforeClosing()){
						return;
					}
					closeGameData();
				}
				
				gd = originPath.parseGameData();
				System.out.println(selected);
				setGameData(gd, originPath);
			} catch (InvalidRessourcePathException | IOException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
				"Could not read selected ressource file : " + e.getMessage(),
				"Inane error",
				JOptionPane.ERROR_MESSAGE);
			} catch (RessourceException e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(this,
				"Could not read selected ressource file : " + e.getMessage(),
				"Inane error",
				JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Performs the tasks (such as potentiel cleanups, and checking if we saved) before closing the current GD.
	 * @return boolean : false if we should cancel the close operation 
	 */
	private boolean checkBeforeClosing(){
		if (modifsOccured()){
			int result = JOptionPane.showConfirmDialog(Window.this,
				"Des modifications n'ont pas été sauvegardées. Voulez vous sauvegarder ?", "Confirm exit", JOptionPane.YES_NO_CANCEL_OPTION);
				System.out.println(result);
				switch (result){
					case JOptionPane.YES_OPTION:
						saveData();
						break;
					case JOptionPane.CANCEL_OPTION:
					case JOptionPane.CLOSED_OPTION:
						return false;
				}
		}
		
		return true;
	}

	public void closeGameData(){
		clearGUI();

		setMenuBar_(preload_bar);

		currentData = null;
		currentFileList = null;
		currentRessourcePath = null;
		modifsOccured = false;
		initializing = true;
		
	}

	private MissingInfoListener missingInfoListener = new MissingInfoListener() {
		@Override public boolean missingAnimationDescriptor(RessourcePath r, Animation anim, NamedAnimationPool<?> c) {
			int res = JOptionPane.showOptionDialog(Window.this, 
            	"Animation " + anim.getName() + " of champion " + c.getDisplayName() + """ 
				 does not have a descriptor file 
				but needs one (contains elements that can't be saved without a descriptor file). 
				Do you want to set a descriptor file for this animation ? 
				(if you don't, the data save will be aborted)""",
			"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);

            if (res != JOptionPane.YES_OPTION) return false;

			return setAnimDescriptor(anim);
		};
	};

	private boolean setAnimDescriptor(Animation anim){
		return (new ChangeDescriptorFilenameForm(this, anim).showForm()) == JOptionPane.OK_OPTION;
	}

	private boolean setCurrentAnimDescriptor() throws WindowStateException{
		AnimationDisplayer ad = getADisplayer();
		Animation anim = ad.getAnimation();
		return setAnimDescriptor(anim);
	}

	private boolean renameAnimSourceImage(Animation anim){
		return (new RenameSourceImageForm(this, anim).showForm()) == JOptionPane.OK_OPTION;
	}

	private boolean renameCurrentAnimSourceImage() throws WindowStateException{
		Animation anim = getCurrentyAnimation();
		return renameAnimSourceImage(anim);
	}

	private boolean changeAnimSourceImage(Animation anim){
		return (new ChangeSourceImageForm(this, anim).showForm()) == JOptionPane.OK_OPTION;
	}

	private boolean changeCurrentAnimSourceImage() throws WindowStateException{
		Animation anim = getCurrentyAnimation();
		return changeAnimSourceImage(anim);
	}

	private boolean renameChampionDescriptor(Champion champion){
		return (new RenameChampionDescriptorForm(this, champion).showForm()) == JOptionPane.OK_OPTION;
	}

	//TODO réellement sauvegarder l'entité/animationpool actuelle
	//j'ai pas la moidre idée de ce que ça voulait dire
	private boolean renamecurrentChampionDescriptor(){
		EntityAnimation anim = getCurrentEntityAnimation();
		NamedAnimationPool<EntityAnimation> owner = currentData.getEntityAnimationOwner(anim);
		if (owner instanceof Champion){
			Champion champion = (Champion)owner;
			return renameChampionDescriptor(champion);
		}
		return false;
	}

	/**
	 * saves the current game data to a given ressource path
	 * @param rPath a ressource path to save the data to.
	 */
	public void saveDataTo(RessourcePath rPath){
		try {

			rPath.saveGameData(currentData, missingInfoListener);
		} catch (GameDataException ex){
			errorPopup("Error : invalid game data.");
			ex.printStackTrace();
		} catch (TransparentGameDataException ex) {
			ex.printStackTrace();
			errorPopup("Error : " + ex.getMessage());
		} catch (IOException ex){
			errorPopup("Error : file system error while writing file " + ex.getMessage());
			ex.printStackTrace();
		}
	}

	/**
	 * Ask the user for a path and saves the current game data to it.
	 */
	private void saveDataAs(){
		try {
			PathChooser chooser = new PathChooser(PathChooser.Mode.DIRECTORY, ".");
			Path selected = chooser.openPath(Window.this);
			if (selected == null) return;

			if (!Files.exists(selected)){
				System.out.println("Specified deirectory doesn't exist. Creating it.");
				Files.createDirectories(selected);
			}

			if (selected.equals(currentRessourcePath.getPath())) {
				saveData();
				return;
			}

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

	public Path makeBackupPath(){
		return Paths.get("backup", "" + Calendar.getInstance().getTime().getTime() + ".zip");
	}

	public Path saveBackup(RessourcePath rPath) throws IOException {
		Path path = rPath.getPath();
		Path backupPath = path.resolve(makeBackupPath());

		rPath.saveAsArchive(currentFileList, backupPath);

		return backupPath;
	}

	/**
	 * Saves the current gamedata to the current ressource path.
	 * If there is none, asks the user for one by falling back to saveDataAs
	 */
	public void saveData(){
		if (currentRessourcePath == null){
			saveDataAs();
			return;
		}

		Path backup_path = null;
		try {
			backup_path = saveBackup(currentRessourcePath);
		} catch (IOException ex){
			ex.printStackTrace();
			if (JOptionPane.showConfirmDialog(this,
					"Error while saving a backup of the Game Data. \n Do you want to save anyway ?", "Editor", 
					JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION){
				return;
			}
		}

		saveDataTo(currentRessourcePath);
		resetDataModified();

		if (backup_path != null){
			if (JOptionPane.showConfirmDialog(this,
					"The Game Data was saved. Do you want to delete the backup I made right before ?", "Editor", 
					JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION){
				
				try {
					Files.delete(backup_path);
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				
			}
		}
	}

	public void updateVisualEditor(){
		displayCanvas.repaint();
	}

	private void clearGUI(){
		displayCanvas.setInteractable();
		editor_controls.show("blank");
	}

	public void setDisplayedObject(){
		clearGUI();
		//TODO reset le menu ?
	}

	public void setDisplayedObject(Object o) throws UnsupportedOperationException{
		throw new UnsupportedOperationException();
	}

	public void setDisplayedObject(EntityAnimation anim){
		InteractableDisplayable current = displayCanvas.getInteractable();
		EntityAnimationEditor editor;
		if (current instanceof EntityAnimationEditor){
			editor = (EntityAnimationEditor)current;
			editor.setAnimation(anim);
		} else {
			editor = new EntityAnimationEditor(anim, this);
			displayCanvas.setInteractable(editor);
		}
		editor_controls.show("EntityAnimation");
		updateCurrentFrameField(editor);
		updateCurrentZoomField(editor);
		repaint();

		setGameDataMenuItems(animationGamedataMenuItems, championGamedataMenuItems);
	}

	public void setDisplayedObject(Animation anim){
		InteractableDisplayable current = displayCanvas.getInteractable();
		AnimationEditor editor;
		if (current instanceof AnimationEditor){
			editor = (AnimationEditor)current;
			editor.setAnimation(anim);
		} else {
			editor = new AnimationEditor(anim, this);
			displayCanvas.setInteractable(editor);
		}
		editor_controls.show("EntityAnimation");
		updateCurrentFrameField(editor);
		updateCurrentZoomField(editor);
		repaint();

		setGameDataMenuItems(animationGamedataMenuItems);
	}

	public InteractableDisplayable getCurrentEditor(){
		return displayCanvas.getInteractable();
	}

	/**
	 * Returns the current Editor of the Canvas as an AnimationDisplayer, or throws is Canvas is not currently holding an AnimationDisplayer.
	 * Cannot return null.  
	 * @return AnimationDisplayer : the current editor of the Canvas
	 * @throws WindowStateException
	 */
	public AnimationDisplayer getADisplayer() throws WindowStateException {
		Displayable disp = displayCanvas.getInteractable();
		if (disp instanceof AnimationDisplayer){
			return (AnimationDisplayer)disp;
		} else {
			throw new WindowStateException("User interacted with EntityAnimation-related control while displayed object was not an EntityAnimationEditor");
		}
	}

	/**
	 * Returns the current Editor of the Canvas as an EADisplayer, or throws is Canvas is not currently holding an EADisplayer.
	 * Cannot return null.  
	 * @return EntityAnimationDisplayer : the current editor of the Canvas
	 * @throws WindowStateException
	 */
	public EntityAnimationDisplayer getEADisplayer() throws WindowStateException {
		Displayable disp = displayCanvas.getInteractable();
		if (disp instanceof EntityAnimationDisplayer){
			return (EntityAnimationDisplayer)disp;
		} else {
			throw new WindowStateException("User interacted with EntityAnimation-related control while displayed object was not an EntityAnimationEditor");
		}
	}

	/**
	 * Returns the current Editor of the Canvas as an EADisplayer, or throws is Canvas is not currently holding an EADisplayer.
	 * Cannot return null.
	 * @return EntityAnimationDisplayer : the current editor of the Canvas
	 * @throws WindowStateException
	 */
	public AnimationEditor getAEditor() throws WindowStateException {
		Displayable disp = displayCanvas.getInteractable();
		if (disp instanceof AnimationEditor){
			return (AnimationEditor)disp;
		} else {
			throw new WindowStateException("User interacted with EntityAnimation-related control while displayed object was not an EntityAnimationEditor");
		}
	}

	/**
	 * Returns the current Editor of the Canvas as an EAEditor, or throws is Canvas is not currently holding an EAEditor.
	 * Cannot return null.
	 * @return EntityAnimationEditor : the current editor of the Canvas
	 * @throws WindowStateException
	 */
	/*/
	public EntityAnimationEditor getEAEditor() throws WindowStateException {
		Displayable disp = displayCanvas.getInteractable();
		if (disp instanceof EntityAnimationEditor){
			return (EntityAnimationEditor)disp;
		} else {
			throw new WindowStateException("User interacted with EntityAnimation-related control while displayed object was not an EntityAnimationEditor");
		}
	}*/

	public AbstractAnimationEditorBackend getAEditorBackend() throws WindowStateException {
		Displayable disp = displayCanvas.getInteractable();
		if (disp instanceof AnimationEditor){
			AbstractAnimationEditorBackend editor_backend = ((EditorFrontend)disp).getBackend();
			return editor_backend;
		} else {
			throw new WindowStateException("User interacted with Animation-related control while displayed object was not an AnimationEditor"); 
		}
	}

	public AbstractEntityAnimationEditorBackend getEAEditorBackend() throws WindowStateException {	
		AbstractAnimationEditorBackend editor_backend = getAEditorBackend();
		if (editor_backend instanceof AbstractEntityAnimationEditorBackend){
			return (AbstractEntityAnimationEditorBackend)editor_backend;
		} else {
			throw new WindowStateException("User interacted with EntityAnimation-related control while displayed object was not an EntityAnimationAnimationEditor"); 
		}
	}

	/**
	 * Returns the Entity Animation being edited, or throws if there is none (currently not editing an Entity Animation).
	 * Cannot return null.
	 * @return EntityAnimation : the current animation. If there is none, throws before returning.
	 * @throws WindowStateException 
	 */
	public EntityAnimation getCurrentEntityAnimation() throws WindowStateException {
		EntityAnimationDisplayer ead = getEADisplayer();
		return ead.getAnimation();
	}

	/**
	 * Returns the Animation being edited, or throws if there is none (currently not editing an Entity Animation).
	 * Cannot return null.
	 * @return Animation : the current animation. If there is none, throws before returning.
	 * @throws WindowStateException 
	 */
	public Animation getCurrentyAnimation() throws WindowStateException {
		AnimationDisplayer ad = getADisplayer();
		return ad.getAnimation();
	}

	private void updateCurrentFrameField(AnimationDisplayer displayer){
		tfCurrentFrame.setText(displayer.getFrameIndex());
	}

	private void updateCurrentZoomField(ZoomingDisplayer displayer){
		tfCurrentZoom.setText(displayer.getZoom());
	}

	public void updateAnimControls(Animation anim, boolean ignoreModifications){
		initializing = ignoreModifications;
		
		tfAnimSpeed.setText(Double.toString(anim.getSpeed()));

		initializing = false;
	}

	public void updateFrameControls(Frame frame, boolean ignoreModifications){
		initializing = ignoreModifications;

		tfFrameDuration.setText(Integer.toString(frame.getDuration()));
		Point origin = frame.getOrigin();
		spinFrameOriginX.setValue(origin.x);
		spinFrameOriginY.setValue(origin.y);

		initializing = false;
	}

	private void updateHitboxTypeSpecificControls(Hitbox hitbox, HitboxType type, boolean ignoreModifications){
		initializing = ignoreModifications;

		hitbox_typespecific_controls.show(type.toString());

		switch (type){
			case WIND:

			break;
			case DAMAGE:
			{
				DamageHitbox damageHitbox = (DamageHitbox)hitbox;
				tfDamages.setText(Double.toString(damageHitbox.damage));;
				tfAngle.setText(Integer.toString(damageHitbox.angle));;
				tfBKB.setText(Double.toString(damageHitbox.base_knockback));
				tfSKB.setText(Double.toString(damageHitbox.scaling_knockback));
				spinHitID.setValue(damageHitbox.hitID);
				spinHitboxPrio.setValue(damageHitbox.priority);
				comboAngleMode.setSelectedValue(damageHitbox.angle_mode);
			}
			break;
			default:
		}

		initializing = false;
	}

	public void updateElementControls(CollisionBox cbox, boolean ignoreModifications){
		initializing = ignoreModifications;

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
			comboHurtboxType.setSelectedValue(hurtbox.type);
		}

		if (cbox instanceof Hitbox){
			element_controls.show("hitbox");
			Hitbox hitbox = (Hitbox)cbox;
			spinHitboxX.setValue(hitbox.x);
			spinHitboxY.setValue(hitbox.y);
			spinHitboxWidth.setValue(hitbox.w);
			spinHitboxHeight.setValue(hitbox.h);
			HitboxType type = hitboxTypes.get(hitbox.getClass());
			comboHitboxType.setSelectedItem(type);
			updateHitboxTypeSpecificControls(hitbox, type, ignoreModifications);
		}

		initializing = false;
	}

	private boolean modifsOccured(){
		return modifsOccured;
	}
}
