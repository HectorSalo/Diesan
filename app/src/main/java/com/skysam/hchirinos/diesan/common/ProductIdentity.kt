package com.skysam.hchirinos.diesan.common

import com.skysam.hchirinos.diesan.common.dataClass.Product

/**
 * Centraliza la regla de identidad de partida de stock.
 *
 * Regla:
 * - Si ambos productos tienen productKey no vacío, se comparan por
 *   productKey + priceByUnit + percentageProfit.
 * - Si alguno no tiene productKey (documentos viejos o partidas que aún
 *   no fueron migradas), se aplica el fallback histórico:
 *   name + priceByUnit + percentageProfit.
 *
 * percentageProfit se mantiene en la identidad por compatibilidad con
 * el comportamiento previo. Cualquier cambio futuro en la regla debe
 * hacerse aquí, no en los llamadores.
 */
object ProductIdentity {

    fun areSameStockPartition(a: Product, b: Product): Boolean {
        val keyA = a.productKey
        val keyB = b.productKey

        return if (!keyA.isNullOrBlank() && !keyB.isNullOrBlank()) {
            keyA == keyB &&
                    a.priceByUnit == b.priceByUnit &&
                    a.percentageProfit == b.percentageProfit
        } else {
            a.name == b.name &&
                    a.priceByUnit == b.priceByUnit &&
                    a.percentageProfit == b.percentageProfit
        }
    }
}
