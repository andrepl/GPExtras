package com.norcode.bukkit.gpextras.persistence;

import com.norcode.bukkit.gpextras.Flag;

import javax.persistence.*;
import java.util.EnumSet;

@Entity
@Table(name="gpextras_claimdata")
public class ClaimBean {

    @Id private Long id;

    @Column private String flags = "";

    @Transient private EnumSet<Flag> flagSet = EnumSet.noneOf(Flag.class);

    public ClaimBean() {}

    @Transient public void initFlags() {
        this.flagSet = Flag.fromString(flags);
    }

    @Transient
    public void setFlag(Flag flag) {
        this.flagSet.add(flag);
        this.setFlags(Flag.serialize(flagSet));
    }

    @Transient
    public boolean hasFlag(Flag flag) {
        return this.flagSet.contains(flag);
    }

    @Transient
    public void unsetFlag(Flag flag) {
        this.flagSet.remove(flag);
        this.setFlags(Flag.serialize(flagSet));
    }

    @Transient
    public void toggleFlag(Flag flag) {
        if (this.flagSet.contains(flag)) {
            this.flagSet.remove(flag);
        } else {
            this.flagSet.add(flag);
        }
        this.setFlags(Flag.serialize(flagSet));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long claimId) {
        this.id = claimId;
    }

    public String getFlags() {
        return flags;
    }

    public void setFlags(String flags) {
        this.flags = flags;
    }
}
