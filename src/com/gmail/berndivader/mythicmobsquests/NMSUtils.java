package com.gmail.berndivader.mythicmobsquests;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

public class NMSUtils {
	
    protected static String vp="";
	
    protected static Class<?> class_CraftItemStack;
    protected static Class<?> class_NBTTagCompound;
    protected static Class<?> class_ItemStack;
    
    protected static Field class_ItemStack_tagField;
    protected static Field class_CraftItemStack_getHandleField;
    
    protected static Method class_NBTTagCompound_removeMethod;
    protected static Method class_NBTTagCompound_setStringMethod;
    protected static Method class_NBTTagCompound_getStringMethod;
    
    static {
    	String cn=Bukkit.getServer().getClass().getName();
    	String[]pkgs=StringUtils.split(cn,'.');
    	if (pkgs.length==5) vp=pkgs[3]+".";
    	try {
    		class_ItemStack=fixBukkitClass("net.minecraft.server.ItemStack");
            class_CraftItemStack=fixBukkitClass("org.bukkit.craftbukkit.inventory.CraftItemStack");
            class_NBTTagCompound=fixBukkitClass("net.minecraft.server.NBTTagCompound");
            
    		class_CraftItemStack_getHandleField=class_CraftItemStack.getDeclaredField("handle");
    		class_CraftItemStack_getHandleField.setAccessible(true);
    		class_ItemStack_tagField = class_ItemStack.getDeclaredField("tag");
    		class_ItemStack_tagField.setAccessible(true);
    		class_NBTTagCompound_setStringMethod=class_NBTTagCompound.getMethod("setString",String.class,String.class);
            class_NBTTagCompound_getStringMethod = class_NBTTagCompound.getMethod("getString", String.class);
    		class_NBTTagCompound_removeMethod=class_NBTTagCompound.getMethod("remove",String.class);
    	} catch (NoSuchFieldException | SecurityException e) {
    		e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
    
    public static Class<?> fixBukkitClass(String s1) throws ClassNotFoundException {
        if (!vp.isEmpty()) {
            s1=s1.replace("org.bukkit.craftbukkit.", "org.bukkit.craftbukkit."+vp);
            s1=s1.replace("net.minecraft.server.", "net.minecraft.server."+vp);
        }

        return NMSUtils.class.getClassLoader().loadClass(s1);
    }
    
    private static Object getHandle(org.bukkit.inventory.ItemStack is) {
    	Object o1;
    	try {
    		o1=class_CraftItemStack_getHandleField.get(is);
    	} catch (Throwable ex) {
    		o1=null;
    	}
    	return o1;
    }
    
    private static Object getTag(Object o1) {
    	Object o2;
    	try {
    		o2=class_ItemStack_tagField.get(o1);
            if (o2==null) {
                class_ItemStack_tagField.set(o1,class_NBTTagCompound.newInstance());
            }
        } catch (Throwable ex) {
        	o2=null;
    	}
    	return o2;
    }    

    public static void setMeta(org.bukkit.inventory.ItemStack is,String s1,String s2) {
    	Object o1=getTag(getHandle(is));
    	if (o1==null) return;
    	try {
    		if (s2==null||s2.length()==0) {
    			class_NBTTagCompound_removeMethod.invoke(o1);
    		} else {
    			class_NBTTagCompound_setStringMethod.invoke(o1,s1,s2);
    		}
    	} catch (Throwable ex) {
    		ex.printStackTrace();
    	}
    }
    
    public static String getMeta(org.bukkit.inventory.ItemStack is,String s1) {
    	Object o1=getTag(getHandle(is));
    	if (o1==null||!class_NBTTagCompound.isInstance(o1)) return null;
    	String s2=null;
    	try {
    		s2=(String)class_NBTTagCompound_getStringMethod.invoke(o1,s1);
    	} catch (Throwable ex) {
    		ex.printStackTrace();
    	}
    	return s2;
    }

}
