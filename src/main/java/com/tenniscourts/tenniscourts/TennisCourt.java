package com.tenniscourts.tenniscourts;

import com.tenniscourts.config.persistence.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table
@NoArgsConstructor
@AllArgsConstructor
public class TennisCourt extends BaseEntity<Long>
{
    @NotNull
    private String name;
}
