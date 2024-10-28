package com.ritense.valtimo.xential.repository

import com.ritense.valtimo.xential.domain.XentialToken
import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface XentialTokenRepository : CrudRepository<XentialToken, UUID>
