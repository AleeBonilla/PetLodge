from datetime import date, timedelta

from app.extensions import db
from app.models.pet import Pet
from app.models.reservation import Reservation
from app.services.pet_service import PetService


def test_create_pet(app, sample_data):
    with app.app_context():
        pet = PetService.create_pet(sample_data["owner_id"], {
            "name": "Luna",
            "species": "cat",
            "breed": "Siamés",
        })
        assert pet is not None
        assert pet.name == "Luna"
        assert pet.owner_id == sample_data["owner_id"]


def test_list_pets_excludes_deleted(app, sample_data):
    with app.app_context():
        # Create a second pet and soft-delete it
        pet2 = Pet(owner_id=sample_data["owner_id"], name="Deleted", species="dog", is_deleted=True)
        db.session.add(pet2)
        db.session.commit()

        pets = PetService.list_pets(sample_data["owner_id"])
        names = [p.name for p in pets]
        assert "Firulais" in names
        assert "Deleted" not in names


def test_get_pet_wrong_owner(app, sample_data):
    with app.app_context():
        # admin_id is not the owner of the pet
        pet, err, code = PetService.get_pet(sample_data["pet_id"], sample_data["admin_id"])
        assert pet is None
        assert code == "PET_NOT_FOUND"


def test_delete_pet_success(app, sample_data):
    with app.app_context():
        pet, err, code = PetService.delete_pet(sample_data["pet_id"], sample_data["owner_id"])
        assert pet is not None
        assert pet.is_deleted is True


def test_delete_pet_with_active_reservation(app, sample_data):
    with app.app_context():
        # Create an active reservation for this pet
        reservation = Reservation(
            owner_id=sample_data["owner_id"],
            pet_id=sample_data["pet_id"],
            room_id=sample_data["std_room_id"],
            check_in_date=date.today() + timedelta(days=1),
            check_out_date=date.today() + timedelta(days=5),
            lodging_type="standard",
            status="confirmed",
            total_price=60000,
        )
        db.session.add(reservation)
        db.session.commit()

        pet, err, code = PetService.delete_pet(sample_data["pet_id"], sample_data["owner_id"])
        assert pet is None
        assert code == "PET_HAS_RESERVATIONS"
