from datetime import date, timedelta

from app.extensions import db
from app.models.reservation import Reservation
from app.models.service import Service
from app.services.reservation_service import ReservationService


def test_check_availability_empty(app, sample_data):
    with app.app_context():
        tomorrow = date.today() + timedelta(days=1)
        next_week = date.today() + timedelta(days=7)
        assert ReservationService.check_availability(sample_data["std_room_id"], tomorrow, next_week) is True


def test_check_availability_conflict(app, sample_data):
    with app.app_context():
        tomorrow = date.today() + timedelta(days=1)
        next_week = date.today() + timedelta(days=7)

        reservation = Reservation(
            owner_id=sample_data["owner_id"],
            pet_id=sample_data["pet_id"],
            room_id=sample_data["std_room_id"],
            check_in_date=tomorrow,
            check_out_date=next_week,
            lodging_type="standard",
            status="confirmed",
            total_price=90000,
        )
        db.session.add(reservation)
        db.session.commit()

        assert ReservationService.check_availability(
            sample_data["std_room_id"],
            tomorrow + timedelta(days=2),
            next_week + timedelta(days=2),
        ) is False


def test_create_reservation_past_date(app, sample_data):
    with app.app_context():
        yesterday = date.today() - timedelta(days=1)
        result, err, code = ReservationService.create_reservation(
            sample_data["owner_id"],
            {
                "pet_id": sample_data["pet_id"],
                "room_id": sample_data["std_room_id"],
                "check_in_date": yesterday,
                "check_out_date": date.today() + timedelta(days=3),
                "lodging_type": "standard",
            },
        )
        assert result is None
        assert code == "INVALID_CHECK_IN"


def test_create_reservation_success(app, sample_data):
    with app.app_context():
        tomorrow = date.today() + timedelta(days=1)
        checkout = date.today() + timedelta(days=4)
        result, err, code = ReservationService.create_reservation(
            sample_data["owner_id"],
            {
                "pet_id": sample_data["pet_id"],
                "room_id": sample_data["std_room_id"],
                "check_in_date": tomorrow,
                "check_out_date": checkout,
                "lodging_type": "standard",
            },
        )
        assert result is not None
        assert result.status == "confirmed"
        assert result.total_price == 15000 * 3


def test_create_reservation_special_with_services(app, sample_data):
    with app.app_context():
        tomorrow = date.today() + timedelta(days=1)
        checkout = date.today() + timedelta(days=4)
        num_nights = 3

        result, err, code = ReservationService.create_reservation(
            sample_data["owner_id"],
            {
                "pet_id": sample_data["pet_id"],
                "room_id": sample_data["spc_room_id"],
                "check_in_date": tomorrow,
                "check_out_date": checkout,
                "lodging_type": "special",
                "service_ids": [sample_data["service_id"]],
            },
        )
        assert result is not None
        assert result.lodging_type == "special"
        # 30000 * 3 nights + 8000 * 3 nights (service)
        expected_price = (30000 * num_nights) + (8000 * num_nights)
        assert result.total_price == expected_price
        assert len(result.services) == 1


def test_services_not_allowed_standard(app, sample_data):
    with app.app_context():
        tomorrow = date.today() + timedelta(days=1)
        checkout = date.today() + timedelta(days=4)
        result, err, code = ReservationService.create_reservation(
            sample_data["owner_id"],
            {
                "pet_id": sample_data["pet_id"],
                "room_id": sample_data["std_room_id"],
                "check_in_date": tomorrow,
                "check_out_date": checkout,
                "lodging_type": "standard",
                "service_ids": [sample_data["service_id"]],
            },
        )
        assert result is None
        assert code == "SERVICES_NOT_ALLOWED"


def test_cancel_already_cancelled(app, sample_data):
    with app.app_context():
        reservation = Reservation(
            owner_id=sample_data["owner_id"],
            pet_id=sample_data["pet_id"],
            room_id=sample_data["std_room_id"],
            check_in_date=date.today() + timedelta(days=10),
            check_out_date=date.today() + timedelta(days=15),
            lodging_type="standard",
            status="cancelled",
            total_price=75000,
        )
        db.session.add(reservation)
        db.session.commit()

        result, err, code = ReservationService.cancel_reservation(reservation.id, sample_data["owner_id"])
        assert result is None
        assert code == "CANNOT_CANCEL"
