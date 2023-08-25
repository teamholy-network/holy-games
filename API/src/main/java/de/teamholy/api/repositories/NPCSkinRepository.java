package de.teamholy.api.repositories;

import de.teamholy.api.bukkit.npc.models.SkinEntry;
import eu.koboo.en2do.repository.AsyncRepository;
import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.UUID;

@Collection("npcskin_collection")
public interface NPCSkinRepository extends Repository<SkinEntry, UUID>, AsyncRepository<SkinEntry, UUID> {
}
