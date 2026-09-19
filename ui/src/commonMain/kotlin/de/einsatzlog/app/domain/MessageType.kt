package de.einsatzlog.app.domain

/**
 * The five fire-service message categories. Domain terms stay German in every
 * UI language (design brief, principle 6); [labelEn] exists for accessibility
 * descriptions and export footnotes.
 */
enum class MessageType(val labelDe: String, val labelEn: String) {
    FUNKSPRUCH("Funkspruch", "Radio message"),
    LAGEMELDUNG("Lagemeldung", "Situation report"),
    AUFTRAG("Auftrag", "Order"),
    ANFORDERUNG("Anforderung", "Request"),
    DOKUMENTATION("Dokumentation", "Note"),
}
