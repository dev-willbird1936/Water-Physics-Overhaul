package net.skds.wpo.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.skds.wpo.WPOConfig;
import net.skds.wpo.fluidphysics.FFluidStatic;
import net.skds.wpo.util.ExtendedFHIS;

import javax.annotation.Nullable;
import java.util.List;
public class AdvancedBucket extends BucketItem {

	public AdvancedBucket(Fluid fluid, Properties builder) {
		super(fluid, builder);
	}

//	@Override
//	public void initializeClient(Consumer<IItemRenderProperties> consumer) {
//		// needed to use custom BlockEntityWithoutLevelRenderer for this item
//		consumer.accept(new IItemRenderProperties() {
//
//			@Override
//			public BlockEntityWithoutLevelRenderer getItemStackRenderer() {
//				return ISTER.getInstance();
//			}
//		});
//	}

	public static AdvancedBucket getBucketForReg(Fluid fluid) {
		Properties prop = new Properties().stacksTo(fluid == Fluids.EMPTY ? 16 : 1)
				.durability(WPOConfig.MAX_FLUID_LEVEL).setNoRepair();
		return new AdvancedBucket(fluid, prop);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		return super.use(worldIn, playerIn, handIn);
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip,
								TooltipFlag flagIn) {
		ExtendedFHIS fh = new ExtendedFHIS(stack, 1000);
		FluidStack fst = fh.getFluid();
		Fluid f = fst.getFluid();
		ChatFormatting form = ChatFormatting.DARK_PURPLE;
		Component texComp = Component.translatable(f.getFluidType().getDescriptionId(fst)).withStyle(form);
		tooltip.add(texComp);
		texComp = Component.literal(fst.getAmount() + " mb");
		tooltip.add(texComp);		
	}

	public static void updateDamage(ItemStack stack) {
		ExtendedFHIS fst = new ExtendedFHIS(stack, 1000);
		int sl = fst.getFluid().getAmount() / FFluidStatic.FCONST;
		stack.setDamageValue(WPOConfig.MAX_FLUID_LEVEL - sl);
	}
}
