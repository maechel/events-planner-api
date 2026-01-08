package com.example.events_planner.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private OffsetDateTime date;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToMany
    @JoinTable(
        name = "event_organizers",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserAccount> organizers = new HashSet<>();

    @ManyToMany
    @JoinTable(
        name = "event_members",
        joinColumns = @JoinColumn(name = "event_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<UserAccount> members = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<Task> tasks = new HashSet<>();

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OffsetDateTime getDate() { return date; }
    public void setDate(OffsetDateTime date) { this.date = date; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }
    public Set<UserAccount> getOrganizers() { return organizers; }
    @SuppressWarnings("unused")
    public void setOrganizers(Set<UserAccount> organizers) { this.organizers = organizers; }
    public Set<UserAccount> getMembers() { return members; }
    @SuppressWarnings("unused")
    public void setMembers(Set<UserAccount> members) { this.members = members; }
    public Set<Task> getTasks() { return tasks; }
    @SuppressWarnings("unused")
    public void setTasks(Set<Task> tasks) { this.tasks = tasks; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    @SuppressWarnings("unused")
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    @SuppressWarnings("unused")
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
