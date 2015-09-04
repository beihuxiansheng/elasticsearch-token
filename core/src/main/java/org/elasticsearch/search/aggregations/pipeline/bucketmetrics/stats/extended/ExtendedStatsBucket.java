begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.search.aggregations.pipeline.bucketmetrics.stats.extended
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|pipeline
operator|.
name|bucketmetrics
operator|.
name|stats
operator|.
name|extended
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|metrics
operator|.
name|stats
operator|.
name|extended
operator|.
name|ExtendedStats
import|;
end_import

begin_comment
comment|/**  * Extended Statistics over a set of buckets  */
end_comment

begin_interface
DECL|interface|ExtendedStatsBucket
specifier|public
interface|interface
name|ExtendedStatsBucket
extends|extends
name|ExtendedStats
block|{ }
end_interface

end_unit

