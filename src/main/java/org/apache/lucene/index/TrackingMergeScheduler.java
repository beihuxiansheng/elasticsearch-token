begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.apache.lucene.index
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentCollections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|TrackingMergeScheduler
specifier|public
class|class
name|TrackingMergeScheduler
block|{
DECL|field|merges
specifier|private
specifier|static
specifier|final
name|ConcurrentMap
argument_list|<
name|Thread
argument_list|,
name|MergePolicy
operator|.
name|OneMerge
argument_list|>
name|merges
init|=
name|ConcurrentCollections
operator|.
name|newConcurrentMap
argument_list|()
decl_stmt|;
DECL|method|setCurrentMerge
specifier|public
specifier|static
name|void
name|setCurrentMerge
parameter_list|(
name|MergePolicy
operator|.
name|OneMerge
name|merge
parameter_list|)
block|{
name|merges
operator|.
name|put
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|,
name|merge
argument_list|)
expr_stmt|;
block|}
DECL|method|removeCurrentMerge
specifier|public
specifier|static
name|void
name|removeCurrentMerge
parameter_list|()
block|{
name|merges
operator|.
name|remove
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getCurrentMerge
specifier|public
specifier|static
name|MergePolicy
operator|.
name|OneMerge
name|getCurrentMerge
parameter_list|()
block|{
return|return
name|merges
operator|.
name|get
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

