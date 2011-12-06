begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.common.compress.lzf.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|compress
operator|.
name|lzf
operator|.
name|util
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
name|Booleans
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|compress
operator|.
name|lzf
operator|.
name|ChunkDecoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|compress
operator|.
name|lzf
operator|.
name|impl
operator|.
name|UnsafeChunkDecoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|compress
operator|.
name|lzf
operator|.
name|impl
operator|.
name|VanillaChunkDecoder
import|;
end_import

begin_comment
comment|/**  * Simple helper class used for loading  * {@link ChunkDecoder} implementations, based on criteria  * such as "fastest available".  *<p/>  * Yes, it looks butt-ugly, but does the job. Nonetheless, if anyone  * has lipstick for this pig, let me know.  *  * @since 0.9  */
end_comment

begin_class
DECL|class|ChunkDecoderFactory
specifier|public
class|class
name|ChunkDecoderFactory
block|{
DECL|field|_instance
specifier|private
specifier|final
specifier|static
name|ChunkDecoderFactory
name|_instance
decl_stmt|;
static|static
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|impl
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// first, try loading optimal one, which uses Sun JDK Unsafe...
name|impl
operator|=
operator|(
name|Class
argument_list|<
name|?
argument_list|>
operator|)
name|Class
operator|.
name|forName
argument_list|(
name|UnsafeChunkDecoder
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{         }
if|if
condition|(
name|impl
operator|==
literal|null
condition|)
block|{
name|impl
operator|=
name|VanillaChunkDecoder
operator|.
name|class
expr_stmt|;
block|}
comment|// ES: Seems like: https://github.com/ning/compress/issues/13, is fixed, so enable by defualt, but only from 0.19
if|if
condition|(
operator|!
name|Booleans
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"compress.lzf.decoder.optimized"
argument_list|)
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|impl
operator|=
name|VanillaChunkDecoder
operator|.
name|class
expr_stmt|;
block|}
name|_instance
operator|=
operator|new
name|ChunkDecoderFactory
argument_list|(
name|impl
argument_list|)
expr_stmt|;
block|}
DECL|field|_implClass
specifier|private
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|ChunkDecoder
argument_list|>
name|_implClass
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|ChunkDecoderFactory
specifier|private
name|ChunkDecoderFactory
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|imp
parameter_list|)
block|{
name|_implClass
operator|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|ChunkDecoder
argument_list|>
operator|)
name|imp
expr_stmt|;
block|}
comment|/*     ///////////////////////////////////////////////////////////////////////     // Public API     ///////////////////////////////////////////////////////////////////////      */
comment|/**      * Method to use for getting decompressor instance that uses the most optimal      * available methods for underlying data access. It should be safe to call      * this method as implementations are dynamically loaded; however, on some      * non-standard platforms it may be necessary to either directly load      * instances, or use {@link #safeInstance()}.      */
DECL|method|optimalInstance
specifier|public
specifier|static
name|ChunkDecoder
name|optimalInstance
parameter_list|()
block|{
try|try
block|{
return|return
name|_instance
operator|.
name|_implClass
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Failed to load a ChunkDecoder instance ("
operator|+
name|e
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"): "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Method that can be used to ensure that a "safe" decompressor instance is loaded.      * Safe here means that it should work on any and all Java platforms.      */
DECL|method|safeInstance
specifier|public
specifier|static
name|ChunkDecoder
name|safeInstance
parameter_list|()
block|{
comment|// this will always succeed loading; no need to use dynamic class loading or instantiation
return|return
operator|new
name|VanillaChunkDecoder
argument_list|()
return|;
block|}
block|}
end_class

end_unit

