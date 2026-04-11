from datetime import datetime, timezone

from app.extensions import db


class Pet(db.Model):
    __tablename__ = "pets"

    id = db.Column(db.Integer, primary_key=True)
    owner_id = db.Column(db.Integer, db.ForeignKey("users.id"), nullable=False)
    name = db.Column(db.String(100), nullable=False)
    species = db.Column(db.String(50), nullable=False)
    breed = db.Column(db.String(100), nullable=True)
    age_years = db.Column(db.Integer, nullable=True)
    weight_kg = db.Column(db.Float, nullable=True)
    photo_url = db.Column(db.String(500), nullable=True)
    medical_notes = db.Column(db.Text, nullable=True)
    care_notes = db.Column(db.Text, nullable=True)
    is_deleted = db.Column(db.Boolean, default=False)
    created_at = db.Column(
        db.DateTime, nullable=False, default=lambda: datetime.now(timezone.utc)
    )
    updated_at = db.Column(
        db.DateTime,
        nullable=False,
        default=lambda: datetime.now(timezone.utc),
        onupdate=lambda: datetime.now(timezone.utc),
    )

    # Relationships
    owner = db.relationship("User", back_populates="pets")
    reservations = db.relationship("Reservation", back_populates="pet", lazy="dynamic")

    def __repr__(self):
        return f"<Pet {self.id} {self.name} ({self.species})>"
