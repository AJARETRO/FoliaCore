package dev.ajaretro.foliaCore.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ItemUtil {

    public static String serializeItems(ItemStack[] items) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeInt(items.length);
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }
            return Base64.getEncoder().encodeToString(outputStream.toByteArray());

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack[] deserializeItems(String base64String) {
        if (base64String == null || base64String.isEmpty()) {
            return new ItemStack[0];
        }

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64.getDecoder().decode(base64String));
             BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {

            int size = dataInput.readInt();
            ItemStack[] items = new ItemStack[size];
            for (int i = 0; i < size; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }
            return items;

        } catch (Exception e) {
            e.printStackTrace();
            return new ItemStack[0];
        }
    }
}