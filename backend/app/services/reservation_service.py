from datetime import date

from sqlalchemy import and_

from app.extensions import db
from app.models.pet import Pet
from app.models.room import Room
from app.models.reservation import Reservation
from app.models.service import Service
from app.models.reservation_service import ReservationService as ReservationServiceModel


class ReservationService:

    @staticmethod
    def check_availability(room_id, check_in, check_out, exclude_reservation_id=None):
        query = Reservation.query.filter(
            Reservation.room_id == room_id,
            Reservation.status.in_(["confirmed", "in_progress"]),
            and_(
                Reservation.check_in_date < check_out,
                Reservation.check_out_date > check_in,
            ),
        )
        if exclude_reservation_id:
            query = query.filter(Reservation.id != exclude_reservation_id)
        return query.first() is None

    @staticmethod
    def create_reservation(owner_id, data):
        # Validate check_in not in the past
        if data["check_in_date"] < date.today():
            return None, "La fecha de ingreso no puede ser en el pasado.", "INVALID_CHECK_IN"

        # Validate pet belongs to owner and is not deleted
        pet = Pet.query.filter_by(id=data["pet_id"], owner_id=owner_id, is_deleted=False).first()
        if not pet:
            return None, "Mascota no encontrada.", "PET_NOT_FOUND"

        # Validate room exists and is active
        room = Room.query.filter_by(id=data["room_id"], is_active=True).first()
        if not room:
            return None, "Habitación no encontrada o no disponible.", "ROOM_NOT_FOUND"

        # Validate availability
        if not ReservationService.check_availability(
            data["room_id"], data["check_in_date"], data["check_out_date"]
        ):
            return None, "La habitación no está disponible en las fechas solicitadas.", "RESERVATION_CONFLICT"

        # Validate services only for special lodging
        service_ids = data.get("service_ids", [])
        if service_ids and data["lodging_type"] != "special":
            return None, "Los servicios adicionales solo están disponibles para hospedaje especial.", "SERVICES_NOT_ALLOWED"

        # Calculate number of nights
        num_nights = (data["check_out_date"] - data["check_in_date"]).days
        total_price = room.price_per_night * num_nights

        reservation = Reservation(
            owner_id=owner_id,
            pet_id=data["pet_id"],
            room_id=data["room_id"],
            check_in_date=data["check_in_date"],
            check_out_date=data["check_out_date"],
            lodging_type=data["lodging_type"],
            status="confirmed",
            notes=data.get("notes"),
        )
        db.session.add(reservation)
        db.session.flush()

        # Add services
        for service_id in service_ids:
            service = Service.query.filter_by(id=service_id, is_active=True).first()
            if not service:
                db.session.rollback()
                return None, f"Servicio con id {service_id} no encontrado.", "SERVICE_NOT_FOUND"

            rs = ReservationServiceModel(
                reservation_id=reservation.id,
                service_id=service.id,
                quantity=1,
                subtotal=service.price * num_nights,
            )
            db.session.add(rs)
            total_price += rs.subtotal

        reservation.total_price = total_price
        db.session.commit()

        return reservation, None, None

    @staticmethod
    def list_reservations(owner_id):
        return Reservation.query.filter_by(owner_id=owner_id).order_by(
            Reservation.created_at.desc()
        ).all()

    @staticmethod
    def get_reservation(reservation_id, owner_id):
        reservation = Reservation.query.filter_by(
            id=reservation_id, owner_id=owner_id
        ).first()
        if not reservation:
            return None, "Reserva no encontrada.", "RESERVATION_NOT_FOUND"
        return reservation, None, None

    @staticmethod
    def cancel_reservation(reservation_id, owner_id):
        reservation = Reservation.query.filter_by(
            id=reservation_id, owner_id=owner_id
        ).first()
        if not reservation:
            return None, "Reserva no encontrada.", "RESERVATION_NOT_FOUND"

        if reservation.status in ["completed", "cancelled"]:
            return None, "No se puede cancelar esta reserva.", "CANNOT_CANCEL"

        reservation.status = "cancelled"
        db.session.commit()
        return reservation, None, None
