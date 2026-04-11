from app.extensions import ma
from marshmallow import fields, validate

SPECIES_CHOICES = ["dog", "cat", "bird", "rabbit", "hamster", "reptile", "other"]


class PetSchema(ma.Schema):
    id = fields.Integer(dump_only=True)
    owner_id = fields.Integer(dump_only=True)
    name = fields.String(required=True)
    species = fields.String(required=True)
    breed = fields.String()
    age_years = fields.Float()
    weight_kg = fields.Float()
    photo_url = fields.String()
    medical_notes = fields.String()
    care_notes = fields.String()
    is_deleted = fields.Boolean(dump_only=True)
    created_at = fields.DateTime(dump_only=True)
    updated_at = fields.DateTime(dump_only=True)


class PetCreateSchema(ma.Schema):
    name = fields.String(required=True)
    species = fields.String(
        required=True,
        validate=validate.OneOf(SPECIES_CHOICES),
    )
    breed = fields.String(load_default=None)
    age_years = fields.Float(load_default=None)
    weight_kg = fields.Float(load_default=None)
    photo_url = fields.String(load_default=None)
    medical_notes = fields.String(load_default=None)
    care_notes = fields.String(load_default=None)


class PetUpdateSchema(ma.Schema):
    name = fields.String(load_default=None)
    species = fields.String(
        load_default=None,
        validate=validate.OneOf(SPECIES_CHOICES),
    )
    breed = fields.String(load_default=None)
    age_years = fields.Float(load_default=None)
    weight_kg = fields.Float(load_default=None)
    photo_url = fields.String(load_default=None)
    medical_notes = fields.String(load_default=None)
    care_notes = fields.String(load_default=None)
