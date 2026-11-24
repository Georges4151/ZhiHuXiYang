USE elderly_care;
SELECT 'activity' as tbl, COUNT(*) as cnt FROM activity
UNION ALL
SELECT 'news_article', COUNT(*) FROM news_article
UNION ALL
SELECT 'health_data', COUNT(*) FROM health_data
UNION ALL
SELECT 'emergency_call', COUNT(*) FROM emergency_call
UNION ALL
SELECT 'feedback', COUNT(*) FROM feedback;
