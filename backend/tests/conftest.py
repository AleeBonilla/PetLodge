from datetime import date, timedelta

import pytest

from app import create_app
from app.extensions import db as _db
from app.models.pet import Pet
from app.models.room import Room
from app.models.service import Service
from app.models.user import User


@pytest.fixture
def app():
    app = create_app("testing")
    with app.app_context():
        _db.create_all()
    yield app
    with app.app_context():
        _db.session.remove()
        _db.drop_all()


@pytest.fixture
def sample_data(app):
    with app.app_context():
        # Owner user
        owner = User(email="owner@test.com", full_name="Owner Test", id_number="1111111111", role="owner")
        owner.set_password("password123")
        _db.session.add(owner)

        # Admin user
        admin = User(email="admin@test.com", full_name="Admin Test", id_number="2222222222", role="admin")
        admin.set_password("admin123")
        _db.session.add(admin)
        _db.session.flush()

        # Pet
        pet = Pet(owner_id=owner.id, name="Firulais", species="dog", breed="Labrador")
        _db.session.add(pet)

        # Standard room
        std_room = Room(number="T-001", name="Test Standard", room_type="standard", price_per_night=15000)
        _db.session.add(std_room)

        # Special room
        spc_room = Room(number="T-002", name="Test Special", room_type="special", price_per_night=30000)
        _db.session.add(spc_room)

        # Service
        service = Service(name="Baño completo", description="Baño con shampoo", price=8000)
        _db.session.add(service)

        _db.session.flush()
        _db.session.commit()

        return {
            "owner_id": owner.id,
            "admin_id": admin.id,
            "pet_id": pet.id,
            "std_room_id": std_room.id,
            "spc_room_id": spc_room.id,
            "service_id": service.id,
        }
