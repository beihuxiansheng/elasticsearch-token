begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.search.facet
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
package|;
end_package

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|ExtendedFacetsTestsMultiShardMultiNodeTests
specifier|public
class|class
name|ExtendedFacetsTestsMultiShardMultiNodeTests
extends|extends
name|ExtendedFacetsTests
block|{
annotation|@
name|Override
DECL|method|numberOfShards
specifier|protected
name|int
name|numberOfShards
parameter_list|()
block|{
return|return
literal|8
return|;
block|}
annotation|@
name|Override
DECL|method|numDocs
specifier|protected
name|int
name|numDocs
parameter_list|()
block|{
return|return
literal|10000
return|;
block|}
block|}
end_class

end_unit

