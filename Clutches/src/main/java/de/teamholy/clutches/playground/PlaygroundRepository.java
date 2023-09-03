package de.teamholy.clutches.playground;

import de.teamholy.clutches.playground.model.PlaygroundPlayer;
import de.teamholy.clutches.playground.model.Settings;
import eu.koboo.en2do.repository.AsyncRepository;
import eu.koboo.en2do.repository.Collection;
import eu.koboo.en2do.repository.Repository;

import java.util.UUID;

@Collection("playground_profile_collection")
public interface PlaygroundRepository extends Repository<Settings, UUID> {
}
