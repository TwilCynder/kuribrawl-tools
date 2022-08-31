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
import java.util.Collection;
import java.util.Comparator;
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
import KBUtil.ui.Canvas;
import KBUtil.ui.CardPanel;
import KBUtil.ui.Displayable;
import KBUtil.ui.IntegerSpinner;
import KBUtil.ui.Interactable;
import KBUtil.ui.MapComboBox;
import KBUtil.ui.MapComboBoxItem;
import KBUtil.ui.PathChooser;
import KBUtil.ui.TwilTextField;
import KBUtil.ui.documentFilters.IntegerDocumentFilter;
import KBUtil.ui.documentFilters.RealNumberDocumentFilter;
import UI.exceptions.WindowStateException;
import UI.forms.ChangeDescriptorFilenameForm;
import UI.forms.ChangeSourceImageForm;
import UI.forms.NewAnimationForm;
import UI.forms.RenameSourceImageForm;
import gamedata.AngleMode;
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
import gamedata.RessourcePath;
import gamedata.WindHitbox;
import gamedata.RessourcePath.MissingInfoListener;
import gamedata.exceptions.GameDataException;
import gamedata.exceptions.InvalidRessourcePathException;
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

	private JMenu animations_menu;

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
		public abstract void stateChanged(EntityAnimationEditor editor, int value);

		public void stateChanged(ChangeEvent e){
			if (!(e.getSource() instanceof IntegerSpinner)) throw new IllegalStateException("IntegerSpinner-specific change listener called on another component");
			IntegerSpinner source = (IntegerSpinner)e.getSource();
			try{
				int value = source.getValueInt();
				EntityAnimationEditor editor = getEAEDitor();
				stateChanged(editor, value);
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

		public abstract void focusLost(EntityAnimationEditor editor, T source) throws NumberFormatException;

		public void focusGained(FocusEvent e){}

		public void focusLost(FocusEvent e){
			if (!componentClass.isInstance(e.getSource())) throw new IllegalStateException("Typed listener specific to " + componentClass.getName() + "used on different component : " + e.getSource());
			T source = (T)e.getSource(); //do not worry
			EntityAnimationEditor editor = getEAEDitor();
			try{
				focusLost(editor, source);
			} catch (NumberFormatException ex){
				System.out.println("Garbage input in tf frame duration");
			}
		}
	}

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
		hurtbox.add(spinHurtboxWidth, "4, 4");

		dummyLabel = new JLabel("height");
		hurtbox.add(dummyLabel, "6, 4, right, default");

		spinHurtboxHeight = new IntegerSpinner();
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
		hitbox.add(spinHitboxWidth, "4, 4");

		dummyLabel = new JLabel("height");
		hitbox.add(dummyLabel, "6, 4, right, default");

		spinHitboxHeight = new IntegerSpinner();
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
				EntityAnimationEditor displayer;
				try {
					displayer = getEAEDitor();
					displayer.incrFrame();
					updateVisualEditor();
					updateCurrentFrameField(displayer);
				} catch (WindowStateException ex){
				}
			}
		});

		btnButtonLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EntityAnimationEditor displayer;
				try {
					displayer = getEAEDitor();
					displayer.decrFrame();
					updateVisualEditor();
					updateCurrentFrameField(displayer);
				} catch (WindowStateException ex){
				}
			}
		});

		btnzoomout.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				EntityAnimationEditor displayer;
				try {
					displayer = getEAEDitor();
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
				EntityAnimationEditor displayer;
				try {
					displayer = getEAEDitor();
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
			public void focusLost(EntityAnimationEditor editor, TwilTextField source){
				EntityAnimation anim = editor.getAnimation();
				double value = Double.parseDouble(source.getText());
				anim.setSpeed(value);

				notifyDataModified();
			}
		});

		tfFrameDuration.addFocusListener(new TwilFocusListener<TwilTextField>(TwilTextField.class) {
			public void focusLost(EntityAnimationEditor editor, TwilTextField source){
				int value = source.getInt();
				Frame frame = editor.getCurrentFrame();
				frame.setDuration(value);

				notifyDataModified();
			}
		});

		spinFrameOriginX.addChangeListener(new SpinChangeListener() {
			public void stateChanged(EntityAnimationEditor editor, int value){
				editor.moveOriginX(value);
				notifyDataModified();
			}
		});

		spinFrameOriginY.addChangeListener(new SpinChangeListener() {
			public void stateChanged(EntityAnimationEditor editor, int value){
				editor.moveOriginY(value);
				notifyDataModified();
			}
		});

		ChangeListener cboxXChangeListener = new SpinChangeListener() {
			@Override
			public void stateChanged(EntityAnimationEditor editor, int value) {
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
			public void stateChanged(EntityAnimationEditor editor, int value) {
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
			public void stateChanged(EntityAnimationEditor editor, int value) {
				CollisionBox cbox = editor.getSelectedCBox();
				if (cbox != null)
					cbox.w = value;
					notifyDataModified();
			}
		};

		spinHurtboxWidth.addChangeListener(cboxWChangeListener);
		spinHitboxWidth.addChangeListener(cboxWChangeListener);

		ChangeListener cboxHChangeListener = new SpinChangeListener() {
			@Override
			public void stateChanged(EntityAnimationEditor editor, int value) {
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
							EntityAnimationDisplayer displayer = getEAEDitor();
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
						EntityAnimationDisplayer displayer = getEAEDitor();
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
							getEAEDitor().setSelectedCBox(newHitbox);
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
			public void focusLost(EntityAnimationEditor editor, JTextField source){
				double value = Double.parseDouble(source.getText());
				DamageHitbox damage_hitbox = (DamageHitbox)editor.getSelectedDamageHitbox();
				damage_hitbox.damage = value;
				notifyDataModified();
			}
		});

		tfAngle.addFocusListener(new TwilFocusListener<TwilTextField>(TwilTextField.class) {
			@Override
			public void focusLost(EntityAnimationEditor editor, TwilTextField source){
				int value = source.getInt();
				DamageHitbox damage_hitbox = (DamageHitbox)editor.getSelectedDamageHitbox();
				damage_hitbox.angle = value;
				notifyDataModified();
			}
		});

		tfBKB.addFocusListener(new TwilFocusListener<JTextField>(JTextField.class) {
			@Override
			public void focusLost(EntityAnimationEditor editor, JTextField source){
				double value = Double.parseDouble(source.getText());
				DamageHitbox damage_hitbox = (DamageHitbox)editor.getSelectedDamageHitbox();
				damage_hitbox.base_knockback = value;
				notifyDataModified();
			}
		});

		tfSKB.addFocusListener(new TwilFocusListener<JTextField>(JTextField.class) {
			@Override
			public void focusLost(EntityAnimationEditor editor, JTextField source){
				double value = Double.parseDouble(source.getText());
				DamageHitbox damage_hitbox = (DamageHitbox)editor.getSelectedDamageHitbox();
				damage_hitbox.scaling_knockback = value;
				notifyDataModified();
			}
		});

		spinHitboxPrio.addChangeListener(new SpinChangeListener() {
			public void stateChanged(EntityAnimationEditor editor, int value){
				DamageHitbox damage_hitbox = (DamageHitbox)editor.getSelectedDamageHitbox();
				damage_hitbox.priority = value;
				notifyDataModified();
			}
		});

		spinHitID.addChangeListener(new SpinChangeListener() {
			public void stateChanged(EntityAnimationEditor editor, int value){
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
							EntityAnimationDisplayer displayer = getEAEDitor();
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

		//============= ACTIONS ==================

		Action testAction = new AbstractAction("Test"){
			public void actionPerformed(ActionEvent e){
				System.out.println("Test !");
			}
		};

		Action saveArchiveAction = new AbstractAction("Save as archive"){
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
							//save
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

		Action saveAction = new AbstractAction("Save") {
			public void actionPerformed(ActionEvent e){
				if (modifsOccured()){
					saveData();
				}
			}
		};

		Action saveAsAction = new AbstractAction("Save as") {
			public void actionPerformed(ActionEvent e){
				saveDataAs();
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


		//============= MENU ==================

		JMenuBar menu_bar = new JMenuBar();

		JMenu dummyMenu = new JMenu("File");

		JMenuItem dummyMenuItem = new JMenuItem(testAction);
		dummyMenu.add(dummyMenuItem);
		dummyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.SHIFT_DOWN_MASK | KeyEvent.CTRL_DOWN_MASK));

		dummyMenuItem = new JMenuItem(saveAction);
		dummyMenu.add(dummyMenuItem);
		dummyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));

		dummyMenu.add(new JMenuItem(saveAsAction));

		dummyMenu.add(new JMenuItem(saveArchiveAction));

		menu_bar.add(dummyMenu);

		animations_menu = new JMenu("Animations");
		menu_bar.add(animations_menu);

		gameDataMenu = new JMenu("Game Data");

		baseGamedataMenuItems = new LinkedList<>();

		dummyMenuItem = new JMenuItem(newAnimationAction);
		baseGamedataMenuItems.add(dummyMenuItem);
		dummyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, KeyEvent.CTRL_DOWN_MASK));

		animationGamedataMenuItems = new LinkedList<>();

		animationGamedataMenuItems.add(new JMenuItem(changeDescriptorAction));

		animationGamedataMenuItems.add(new JMenuItem(renameSourceImageAction));

		animationGamedataMenuItems.add(new JMenuItem(changeSourceImageAction));

		addItemsToMenu(gameDataMenu, baseGamedataMenuItems);
		menu_bar.add(gameDataMenu);

		setJMenuBar(menu_bar);

		//=========== SHORTCUTS ==================

		/*getRootPane().getInputMap().put(KeyStroke.getKeyStroke("S"),"doSomething");
		getRootPane().getActionMap().put("doSomething",
				testAction);*/

		//========== WINDOW LISTENER =============

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e){
				if (modifsOccured()){
					int result = JOptionPane.showConfirmDialog(Window.this,
						"Des modifications n'ont pas été sauvegardées. Voulez vous sauvegarder ?", "Confirm exit", JOptionPane.YES_NO_CANCEL_OPTION);

						switch (result){
							case JOptionPane.YES_OPTION:
								saveData();
								break;
							case JOptionPane.CANCEL_OPTION:
							case JOptionPane.CLOSED_OPTION:
								return;
						}
				}

				System.exit(0);
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

	private void addItemsToMenu(JMenu menu, Collection<JMenuItem> items) {
		for (var item : items){
			menu.add(item);
		}
	}

	private void setGameDataMenuItems(Collection<JMenuItem> items){
		gameDataMenu.removeAll();
		addItemsToMenu(gameDataMenu, baseGamedataMenuItems);
		if (items == null) return;
		gameDataMenu.addSeparator();
		addItemsToMenu(gameDataMenu, items);
	}

	private void initAnimationsMenu(GameData gd){
		animations_menu.removeAll();

		for (Champion c : gd){
			JMenu champion_submenu = new JMenu(c.getDislayName());
            for (EntityAnimation anim : c){
				champion_submenu.add(new AnimationMenuItem(anim, this));
            }
			animations_menu.add(champion_submenu);
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
	} 

	private MissingInfoListener missingInfoListener = new MissingInfoListener() {
		@Override public boolean missingEntityAnimationDescriptor(RessourcePath r, EntityAnimation anim, Champion c) {
			int res = JOptionPane.showOptionDialog(Window.this, 
            	"Animation " + anim.getName() + " of champion " + c.getDislayName() + """ 
				 does not have a descriptor file 
				but needs one (contains elements that can't be saved without a descriptor file). 
				Do you want to set a descriptor file for this animation ? 
				(if you don't, the data save will be aborted)""",
			"Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, null, null);

            if (res != JOptionPane.YES_OPTION) return false;

			return setAnimDescriptor(anim);
		};
	};

	private Collection<JMenuItem> baseGamedataMenuItems;
	private Collection<JMenuItem> animationGamedataMenuItems;
	private JMenu gameDataMenu;

	private boolean setAnimDescriptor(EntityAnimation anim){
		return (new ChangeDescriptorFilenameForm(this, anim).showForm()) == JOptionPane.OK_OPTION;
	}

	private boolean setCurrentAnimDescriptor() throws WindowStateException{
		EntityAnimationEditor ead = getEAEDitor();
		EntityAnimation anim = ead.getAnimation();
		return setAnimDescriptor(anim);
	}

	private boolean renameAnimSourceImage(EntityAnimation anim){
		return (new RenameSourceImageForm(this, anim).showForm()) == JOptionPane.OK_OPTION;
	}

	private boolean renameCurrentAnimSourceImage() throws WindowStateException{
		EntityAnimationEditor ead = getEAEDitor();
		EntityAnimation anim = ead.getAnimation();
		return renameAnimSourceImage(anim);
	}

	private boolean changeAnimSourceImage(EntityAnimation anim){
		return (new ChangeSourceImageForm(this, anim).showForm()) == JOptionPane.OK_OPTION;
	}

	private boolean changeCurrentAnimSourceImage() throws WindowStateException{
		EntityAnimationEditor ead = getEAEDitor();
		EntityAnimation anim = ead.getAnimation();
		return changeAnimSourceImage(anim);
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
		PathChooser chooser = new PathChooser(PathChooser.Mode.DIRECTORY, ".");
				Path selected = chooser.openPath(Window.this);
				if (selected == null) return;

				if (!Files.exists(selected)){
					System.out.println("Specified deirectory doesn't exist. Creating it.");
					selected.toFile().mkdirs();
				}

				if (selected.equals(currentRessourcePath.getPath())) {
					saveData();
					return;
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

	/**
	 * Saves the current gamedata to the current ressource path.
	 * If there is none, asks the user for one by falling back to saveDataAs
	 */
	public void saveData(){
		if (currentRessourcePath == null){
			saveDataAs();
			return;
		}
		saveDataTo(currentRessourcePath);
		resetDataModified();
	}

	public void updateVisualEditor(){
		displayCanvas.repaint();
	}

	public void setDisplayedObject(EntityAnimation anim){
		Interactable current = displayCanvas.getInteractable();
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

		setGameDataMenuItems(animationGamedataMenuItems);
	}

	public Interactable getCurrentEditor(){
		return displayCanvas.getInteractable();
	}

	/**
	 * Returns the current Editor of the Canvas as an EAEditor, or throws is Canvas is not currently holding an EAEditor.
	 * Cannot return null.
	 * @return EntityAnimationEditor : the current editor of the Canvas
	 * @throws WindowStateException
	 */
	public EntityAnimationEditor getEAEDitor() throws WindowStateException {
		Displayable disp = displayCanvas.getInteractable();
		if (disp instanceof EntityAnimationEditor){
			return (EntityAnimationEditor)disp;
		} else {
			throw new WindowStateException("User interacted with EntityAnimation-related control while displayed object was not an EntityAnimationEditor");
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

	public void updateAnimControls(EntityAnimation anim, boolean ignoreModifications){
		initializing = ignoreModifications;

		tfAnimSpeed.setText(Double.toString(anim.getSpeed()));

		initializing = false;
	}

	public void updateFrameControls(Frame frame, EntityFrame entity_frame, boolean ignoreModifications){
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
