begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
package|;
end_package

begin_comment
comment|/**  * Query builder which allow setting some boost  */
end_comment

begin_interface
DECL|interface|BoostableQueryBuilder
specifier|public
interface|interface
name|BoostableQueryBuilder
parameter_list|<
name|B
extends|extends
name|BoostableQueryBuilder
parameter_list|<
name|B
parameter_list|>
parameter_list|>
block|{
comment|/**      * Sets the boost for this query.  Documents matching this query will (in addition to the normal      * weightings) have their score multiplied by the boost provided.      */
DECL|method|boost
specifier|public
name|B
name|boost
parameter_list|(
name|float
name|boost
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

