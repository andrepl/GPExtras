package com.norcode.bukkit.gpextras.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: andre
 * Date: 6/7/13
 * Time: 12:48 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IPersistence {
    public void onEnable();
    public void reload();
    public ClaimBean getClaimData(long ClaimId);
    public void onDisable();
}
