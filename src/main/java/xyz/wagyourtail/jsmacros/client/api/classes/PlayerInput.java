package xyz.wagyourtail.jsmacros.client.api.classes;

import com.google.gson.Gson;
import net.minecraft.util.MovementInput;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An object, that combines all possible player inputs
 *
 * @author NotSomeBot
 * @since 1.4.0
 */
@SuppressWarnings("unused")
public class PlayerInput {
    private static final Gson gson = new Gson();
    public float movementForward;
    public float movementSideways;
    public float yaw;
    public float pitch;
    public boolean jumping;
    public boolean sneaking;
    public boolean sprinting;

    /**
     * Creates a new {@code PlayerInput} Object with all values set either to 0 or false
     *
     * @see #PlayerInput(float, float, float, float, boolean, boolean, boolean)
     * @since 1.4.0
     */
    public PlayerInput() {
        this(0.0F, 0.0F, 0.0F, 0.0F, false, false, false);
    }

    /**
     * Creates a new {@code PlayerInput} Object with all other values set either to 0 or false
     *
     * @param movementForward  1 = forward input (W); 0 = no input; -1 = backward input (S)
     * @param movementSideways 1 = left input (A); 0 = no input; -1 = right input (D)
     * @param yaw              yaw of the player
     * @see #PlayerInput(float, float, float, float, boolean, boolean, boolean)
     * @since 1.4.0
     */
    public PlayerInput(float movementForward, float movementSideways, float yaw) {
        this(movementForward, movementSideways, yaw, 0.0, false, false, false);
    }

    /**
     * Creates a new {@code PlayerInput} Object with all other values set either to 0 or false
     *
     * @param movementForward 1 = forward input (W); 0 = no input; -1 = backward input (S)
     * @param yaw             yaw of the player
     * @param jumping         jump input
     * @param sprinting       sprint input
     * @see #PlayerInput(float, float, float, float, boolean, boolean, boolean)
     * @since 1.4.0
     */
    public PlayerInput(float movementForward, float yaw, boolean jumping, boolean sprinting) {
        this(movementForward, 0.0F, yaw, 0.0, jumping, false, sprinting);
    }

    /**
     * Creates a new {@code PlayerInput} Object from a minecraft input with the missing values extra
     *
     * @param input     Minecraft Input to be converted.
     * @param yaw       yaw of the player
     * @param pitch     pitch of the player
     * @param sprinting sprint input
     * @see #PlayerInput(float, float, float, float, boolean, boolean, boolean)
     * @since 1.4.0
     */
    public PlayerInput(MovementInput input, float yaw, float pitch, boolean sprinting) {
        this(input.moveForward, input.moveStrafe, yaw, pitch, input.jump, input.sneak, sprinting);
    }

    /**
     * Creates a new {@code PlayerInput} Object with all double values converted to floats
     *
     * @param movementForward  1 = forward input (W); 0 = no input; -1 = backward input (S)
     * @param movementSideways 1 = left input (A); 0 = no input; -1 = right input (D)
     * @param yaw              yaw of the player
     * @param pitch            pitch of the player
     * @param jumping          jump input
     * @param sneaking         sneak input
     * @param sprinting        sprint input
     * @see #PlayerInput(float, float, float, float, boolean, boolean, boolean)
     * @since 1.4.0
     */
    public PlayerInput(double movementForward, double movementSideways, double yaw, double pitch, boolean jumping, boolean sneaking, boolean sprinting) {
        this((float) movementForward, (float) movementSideways, (float) yaw, (float) pitch, jumping, sneaking, sprinting);
    }

    /**
     * Creates a new {@code PlayerInput} Object
     *
     * @param movementForward  1 = forward input (W); 0 = no input; -1 = backward input (S)
     * @param movementSideways 1 = left input (A); 0 = no input; -1 = right input (D)
     * @param yaw              yaw of the player
     * @param pitch            pitch of the player
     * @param jumping          jump input
     * @param sneaking         sneak input
     * @param sprinting        sprint input
     * @since 1.4.0
     */
    public PlayerInput(float movementForward, float movementSideways, float yaw, float pitch, boolean jumping, boolean sneaking, boolean sprinting) {
        this.movementForward = movementForward;
        this.movementSideways = movementSideways;
        this.yaw = yaw;
        this.pitch = pitch;
        this.jumping = jumping;
        this.sneaking = sneaking;
        this.sprinting = sprinting;
    }

    /**
     * Creates a clone {@code PlayerInput} Object
     *
     * @param input the {@code PlayerInput} object to be cloned
     * @since 1.4.0
     */
    public PlayerInput(PlayerInput input) {
        this.movementForward = input.movementForward;
        this.movementSideways = input.movementSideways;
        this.yaw = input.yaw;
        this.pitch = input.pitch;
        this.jumping = input.jumping;
        this.sneaking = input.sneaking;
        this.sprinting = input.sprinting;
    }

    /**
     * Parses each row of CSV string into a {@code PlayerInput}.
     * The capitalization of the header matters.<br>
     * About the columns:
     * <ul>
     *   <li> {@code movementForward} and {@code movementSideways} as a number</li>
     *   <li>{@code yaw} and {@code pitch} as an absolute number</li>
     *   <li>{@code jumping}, {@code sneaking} and {@code sprinting} have to be boolean</li>
     * </ul>
     * <p>
     * The separation must be a "," it's a csv...(but spaces don't matter)<br>
     * Quoted values don't work
     *
     * @param csv CSV string to be parsed
     * @return {@code List<PlayerInput>} Each row parsed as a {@code PlayerInput}
     * @see #PlayerInput(float, float, float, float, boolean, boolean, boolean)
     * @since 1.4.0
     */
    public static List<PlayerInput> fromCsv(String csv) throws NoSuchFieldException, IllegalAccessException {
        String[] rows = csv.replace(" ", "").split("\n");
        List<PlayerInput> output = new ArrayList<>();
        String[] headers = rows[0].split(",");
        String[] row;
        Map<String, String> mappedRow;
        for (int rowNo = 1; rowNo < rows.length; rowNo++) {
            row = rows[rowNo].split(",");
            if (row.length == headers.length) {
                mappedRow = new HashMap<>();
                for (int i = 0; i < row.length; i++) {
                    mappedRow.put(headers[i], row[i]);
                }
                output.add(fromMap(mappedRow));
            }
        }
        return output;
    }

    /**
     * Parses a JSON string into a {@code PlayerInput} Object
     * For details see {@code PlayerInput.fromCsv()}, on what has to be present.<br>
     * Capitalization of the keys matters.
     *
     * @param json JSON string to be parsed
     * @return The JSON parsed into a {@code PlayerInput}
     * @see #fromCsv(String)
     * @since 1.4.0
     */
    public static PlayerInput fromJson(String json) throws NoSuchFieldException, IllegalAccessException {
        Map<String, String> map = new HashMap<>();
        map = (Map<String, String>) gson.fromJson(json, map.getClass());
        map = map.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().toLowerCase().replace("\"", ""), e -> e.getValue().replace("\"", "")));
        return fromMap(map);
    }


    /**
     * Converts a Map, which has all keys lowercase and that contains all the prerequisites listed in {@code PlayerInput.fromCSV}.
     *
     * @param input Map to be converted
     * @return The map converted into a {@code PlayerInput}
     * @see #fromCsv(String)
     * @since 1.4.0
     */
    private static PlayerInput fromMap(Map<String, String> input) throws NoSuchFieldException, IllegalAccessException {
        PlayerInput playerInput = new PlayerInput();
        for (Map.Entry<String, String> entry : input.entrySet()) {
            Field field = PlayerInput.class.getDeclaredField(entry.getKey());
            if (Modifier.isPrivate(field.getModifiers())) throw new IllegalAccessException();
            if (float.class.isAssignableFrom(field.getType())) {
                field.set(playerInput, Float.valueOf(entry.getValue()));
            } else if (boolean.class.isAssignableFrom(field.getType())) {
                field.set(playerInput, Boolean.valueOf(entry.getValue()));
            }
        }
        return playerInput;
    }

    /**
     * Converts the current object into a string.
     * This can be used to convert current inputs created using {@code Player.getCurrentPlayerInput()}
     * to either JSON or CSV.
     * <p>
     * The output can be converted into a PlayerInput object again by using either
     * {@code fromCsv(String, String)} or {@code fromJson(String)}.
     *
     * @param varNames whether to include variable Names(=JSON) or not(=CSV)
     * @return The {@code PlayerInput} object as a string
     * @since 1.4.0
     */
    public String toString(boolean varNames) {
        StringBuilder stringBuilder = new StringBuilder();

        for (Field field : this.getClass().getDeclaredFields()) {
            if (field.getType().equals(Gson.class)) {
                continue;
            }
            field.setAccessible(true);

            if (varNames) {
                stringBuilder.append("\"");
                stringBuilder.append(field.getName());
                stringBuilder.append("\": ");
            }

            try {
                if (field.getType().equals(float.class)) {
                    stringBuilder.append(field.getFloat(this));
                } else {
                    stringBuilder.append(field.get(this).toString());
                }

            } catch (IllegalAccessException ignored) {
            }
            stringBuilder.append(", ");
        }
        stringBuilder.setLength(stringBuilder.length() - 2);
        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("PlayerInput{");
        String prefix = "";
        for (Field field : PlayerInput.class.getDeclaredFields()) {
            if (Modifier.isPrivate(field.getModifiers())) continue;
            try {
                stringBuilder
                        .append(prefix)
                        .append(field.getName())
                        .append("=")
                        .append(field.get(this));
                prefix = ", ";
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        stringBuilder.append("}");

        return stringBuilder.toString();
    }

    @Override
    public PlayerInput clone() {
        return new PlayerInput(this);
    }
}
