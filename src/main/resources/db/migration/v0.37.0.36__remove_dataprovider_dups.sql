-- This is faster then doing the query in one go as it seems this is done in memory whereas a larger query operates on disk which is much slower

WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET        0) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET   250000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET   500000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET   750000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  1000000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  1250000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  1500000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  1750000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  2000000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  2250000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  2500000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  2750000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  3000000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  3250000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  3500000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  3750000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  4000000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  4250000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  4500000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
WITH ids AS (SELECT id FROM crossreference_ids_to_delete ORDER BY id LIMIT 250000 OFFSET  4750000) DELETE FROM crossreference WHERE id IN (SELECT id FROM ids);
