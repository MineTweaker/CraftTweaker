package com.blamejared.crafttweaker.impl.loot.modifiers;

import com.blamejared.crafttweaker.api.annotations.ZenRegister;
import com.blamejared.crafttweaker.api.item.IIngredient;
import com.blamejared.crafttweaker.api.item.IItemStack;
import com.blamejared.crafttweaker.api.loot.modifiers.ILootModifier;
import com.blamejared.crafttweaker.api.util.IntegerRange;
import com.blamejared.crafttweaker.impl.item.MCItemStack;
import com.blamejared.crafttweaker.impl.item.MCWeightedItemStack;
import com.blamejared.crafttweaker.impl_native.loot.ExpandLootContext;
import com.blamejared.crafttweaker_annotations.annotations.Document;
import com.google.common.collect.Lists;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.loot.LootContext;
import net.minecraft.util.Util;
import net.minecraftforge.common.util.Lazy;
import org.openzen.zencode.java.ZenCodeType;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Holds a set of implementations of {@link ILootModifier} of common usage.
 *
 * These can be used freely instead of rewriting the same code more than once. They are also guaranteed to behave
 * correctly.
 */
@ZenRegister
@ZenCodeType.Name("crafttweaker.api.loot.modifiers.CommonLootModifiers")
@Document("vanilla/api/loot/modifiers/CommonLootModifiers")
public final class CommonLootModifiers {
    private static final Lazy<ILootModifier> IDENTITY = Lazy.concurrentOf(() -> (loot, context) -> loot);
    private static final Lazy<ILootModifier> LOOT_CLEARING_MODIFIER = Lazy.concurrentOf(() -> (loot, context) -> new ArrayList<>());
    private static final Lazy<ILootModifier> SILK_TOUCH = Lazy.concurrentOf(() -> ((loot, currentContext) -> {
        if (ExpandLootContext.getTool(currentContext).getEnchantmentLevel(Enchantments.SILK_TOUCH) != 0) {
            BlockState blockState = ExpandLootContext.getBlockState(currentContext);
            if (blockState != null) {
                return Lists.newArrayList(new MCItemStack(blockState.getBlock().asItem().getDefaultInstance()));
            }
        }
        return loot;
    }));

    // Addition methods
    /**
     * Adds the given {@link IItemStack} to the drops.
     *
     * @param stack The stack to add
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier add(final IItemStack stack) {
        return stack.isEmpty() ? IDENTITY.get() : (loot, context) -> addItem(loot, stack);
    }

    /**
     * Adds the given {@link IItemStack} with random amount to the drops
     * @param stack The stack to add
     * @param amountRange The range of the stack amount
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier add(final IItemStack stack, IntegerRange amountRange) {
        if (stack.isEmpty()) return IDENTITY.get();
        if (amountRange.getMin() < 0) throw new IllegalArgumentException("Minimum must be more than 0");
        return ((loot, currentContext) -> {
            final int amount = amountRange.getRandomValue(ExpandLootContext.getRandom(currentContext));
            return addItem(loot, stack, amount);
        });
    }

    /**
     * Adds the given {@link MCWeightedItemStack} to the drops
     * @param stack The weighted stack to add
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier add(final MCWeightedItemStack stack) {
        if (stack.getItemStack().isEmpty()) return IDENTITY.get();
        return ((loot, currentContext) -> {
            if (ExpandLootContext.getRandom(currentContext).nextDouble() < stack.getWeight()) {
                return addItem(loot, stack.getItemStack());
            } else {
                return loot;
            }
        });
    }

    /**
     * Adds all the given {@link IItemStack} to the drops.
     *
     * @param stacks The stacks to add
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier addAll(final IItemStack... stacks) {
        final List<IItemStack> stacksToAdd = notEmpty(Arrays.stream(stacks)).collect(Collectors.toList());
        return (loot, context) -> Util.make(new ArrayList<>(loot), it -> it.addAll(stacksToAdd.stream().map(IItemStack::copy).collect(Collectors.toList())));
    }

    /**
     * Adds all the given {@link MCWeightedItemStack} to the drops.
     *
     * @param stacks The stacks to add
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier addAll(final MCWeightedItemStack... stacks) {
        return chaining(Arrays.stream(stacks).map(CommonLootModifiers::add));
    }

    /**
     * Adds the given {@link IItemStack} with random amount to the drops
     * @param stacksMap A map of key-value pairs dictating the stacks with their amount range.
     * @return An {@link ILootModifier} that carries out the operation.
     */

    @ZenCodeType.Method
    public static ILootModifier addAll(final Map<IItemStack, IntegerRange> stacksMap) {
        return chaining(stacksMap.entrySet().stream().map(it -> add(it.getKey(), it.getValue())));
    }

    /**
     * Adds the give {@link IItemStack} with random amount and enchantment bonus, following binomial distribution.
     * (n = EnchantmentLevel + extra, p = probability)
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier addItemWithEnchantmentBinomialBonusCount(final IItemStack stack, Enchantment enchantment, IntegerRange range, int extra, float probability) {
        if (stack.isEmpty()) return IDENTITY.get();
        return ((loot, currentContext) -> {
            int enchantmentLevel = ExpandLootContext.getTool(currentContext).getEnchantmentLevel(enchantment);
            Random random = currentContext.getRandom();
            return addItem(loot, stack, binomial(random, enchantmentLevel + extra, probability) + range.getRandomValue(random));
        });
    }

    /**
     * Adds the give {@link IItemStack} with random amount and enchantment bonus, following uniform distribution.
     * It ranges from <code>range.min</code> and <code>range.getMax() + EnchantmentLevel * bonusMultiplier</code>
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier addItemWithEnchantmentUniformBonusCount(final IItemStack stack, Enchantment enchantment, IntegerRange range, int bonusMultiplier) {
        if (stack.isEmpty()) return IDENTITY.get();
        return ((loot, currentContext) -> {
            final IntegerRange newRange = IntegerRange.create(range.getMin(), range.getMax() + ExpandLootContext.getTool(currentContext).getEnchantmentLevel(enchantment) * bonusMultiplier);
            return addItem(loot, stack, newRange.getRandomValue(ExpandLootContext.getRandom(currentContext)));
        });
    }

    /**
     * Adds the give {@link IItemStack} with random amount and enchantment bonus, following vanilla default ore drops logic.
     * Vanilla default ore drops logic: <p>
     * There is a (EnchantmentLevel)/(EnchantmentLevel + 2) chance that to multiply the origin value with (2 to (EnchantmentLevel + 1)) and a 2/(EnchantmentLevel + 2) chance that to keep the origin value.
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier addItemWithEnchantmentOreDropsBonusCount(final IItemStack stack, Enchantment enchantment, IntegerRange range) {
        if (stack.isEmpty()) return IDENTITY.get();
        return ((loot, currentContext) -> {
            final Random random = currentContext.getRandom();
            final int originValue = range.getRandomValue(random);
            final int level = ExpandLootContext.getTool(currentContext).getEnchantmentLevel(enchantment);
            if (level > 0) {
                int i = random.nextInt(level + 2) - 1;
                if (i < 0) {
                    i = 0;
                }

                return addItem(loot, stack, originValue * (i + 1));
            } else {
                return addItem(loot, stack, originValue);
            }
        });
    }

    /**
     * Adds the give {@link IItemStack} with random amount and fortune bonus, following binomial distribution.
     * (n = EnchantmentLevel + extra, p = probability)
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier addItemWithFortuneBinomialBonusCount(final IItemStack stack, IntegerRange range, int extra, float probability) {
        return addItemWithEnchantmentBinomialBonusCount(stack, Enchantments.FORTUNE, range, extra, probability);
    }

    /**
     * Adds the give {@link IItemStack} with random amount and fortune bonus, following uniform distribution.
     * It ranges from <code>range.min</code> and <code>range.getMax() + EnchantmentLevel * bonusMultiplier</code>
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier addItemWithFortuneUniformBonusCount(final IItemStack stack, IntegerRange range, int bonusMultiplier) {
        return addItemWithEnchantmentUniformBonusCount(stack, Enchantments.FORTUNE, range, bonusMultiplier);
    }

    /**
     * Adds the give {@link IItemStack} with random amount and fortune bonus, following vanilla default ore drops logic.<p>
     * Vanilla default ore drops logic: <p>
     * There is a (EnchantmentLevel)/(EnchantmentLevel + 2) chance that to multiply the origin value with (2 to (EnchantmentLevel + 1)) and a 2/(EnchantmentLevel + 2) chance that to keep the origin value.
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier addItemWithFortuneOreDropsBonusCount(final IItemStack stack, IntegerRange range) {
        return addItemWithEnchantmentOreDropsBonusCount(stack, Enchantments.FORTUNE, range);
    }

    // Replacement methods
    /**
     * Replaces every instance of the targeted {@link IIngredient} with the replacement {@link IItemStack}.
     *
     * In this case, a simple matching procedure is used, where every stack that matches the given <code>target</code>
     * is replaced by the <code>replacement</code> without considering stack size. If stack size is to be preserved,
     * refer to {@link #replaceStackWith(IItemStack, IItemStack)}.
     *
     * @param target The target to replace.
     * @param replacement The replacement to use.
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier replaceWith(final IIngredient target, final IItemStack replacement) {
        return streaming((loot, context) -> replacing(loot, target, replacement));
    }

    /**
     * Replaces every instance of the targeted {@link IIngredient}s with their corresponding replacement
     * {@link IItemStack}.
     *
     * In this case, a simple matching procedure is used, where every stack that matches the key of the pair is replaced
     * by the corresponding value, without considering stack size. If stack size is to be preserved, refer to
     * {@link #replaceAllStacksWith(Map)}.
     *
     * @param replacementMap A map of key-value pairs dictating the target to replace along with their replacement.
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier replaceAllWith(final Map<IIngredient, IItemStack> replacementMap) {
        return chaining(replacementMap.entrySet().stream().map(it -> replaceWith(it.getKey(), it.getValue())));
    }
    
    /**
     * Replaces every instance of the targeted {@link IItemStack} with the replacement {@link IItemStack},
     * proportionally.
     *
     * As an example, if the loot drops 5 carrots and this loot modifier runs with 2 carrots as the <code>target</code>
     * and 1 potato as the <code>replacement</code>, the loot will be modified to 2 potatoes and 1 carrot. This happens
     * because every 2-carrot stack will be actively replaced by a 1-potato stack, without exceptions.
     *
     * This loot modifier acts differently than {@link #replaceWith(IIngredient, IItemStack)}, where a simpler approach
     * is used.
     *
     * @param target The target to replace.
     * @param replacement The replacement to use.
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier replaceStackWith(final IItemStack target, final IItemStack replacement) {
        return streaming((loot, context) -> replacingExactly(loot, target, replacement));
    }
    
    /**
     * Replaces every instance of the targeted {@link IItemStack}s with the replacement {@link IItemStack}s,
     * proportionally.
     *
     * As an example, if the loot drops 5 carrots and this loot modifier runs with 2 carrots as the key of a pair and 1
     * potato as the corresponding value, the loot will be modified to 2 potatoes and 1 carrot. This happens because
     * every 2-carrot stack will be actively replaced by a 1-potato stack, without exceptions.
     *
     * This loot modifier acts differently than {@link #replaceAllWith(Map)}, where a simpler approach is used.
     *
     * @param replacementMap A map of key-value pairs dictating the target to replace along with their replacement.
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier replaceAllStacksWith(final Map<IItemStack, IItemStack> replacementMap) {
        return chaining(replacementMap.entrySet().stream().map(it -> replaceStackWith(it.getKey(), it.getValue())));
    }

    // Removal methods
    /**
     * Removes every instance of the targeted {@link IIngredient} from the drops.
     *
     * @param target The {@link IIngredient} to remove.
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier remove(final IIngredient target) {
        return replaceWith(target, MCItemStack.EMPTY.get());
    }

    /**
     * Removes every instance of all the targeted {@link IIngredient}s from the drops.
     *
     * @param targets The {@link IIngredient}s to remove.
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier removeAll(final IIngredient... targets) {
        return chaining(Arrays.stream(targets).map(CommonLootModifiers::remove));
    }

    /**
     * Clears the entire drop list.
     *
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier clearLoot() {
        return LOOT_CLEARING_MODIFIER.get();
    }

    /**
     * Sets drop list to the block itself when silk touch
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier silkTouch() {
        return SILK_TOUCH.get();
    }

    // Additional utility methods
    /**
     * Chains the given list of {@link ILootModifier}s to be executed one after the other.
     *
     * @param modifiers The modifier list.
     * @return An {@link ILootModifier} that carries out the operation.
     */
    @ZenCodeType.Method
    public static ILootModifier chaining(final ILootModifier... modifiers) {
        return chaining(Arrays.stream(modifiers));
    }
    
    // Private utility stuff
    private static ILootModifier streaming(final BiFunction<Stream<IItemStack>, LootContext, Stream<IItemStack>> consumer) {
        return (loot, context) -> consumer.apply(loot.stream(), context).collect(Collectors.toList());
    }
    
    private static ILootModifier chaining(final Stream<ILootModifier> chain) {
        return chain.reduce(IDENTITY.get(), (first, second) -> (loot, context) -> second.applyModifier(first.applyModifier(loot, context), context));
    }

    private static List<IItemStack> addItem(final List<IItemStack> stacks, IItemStack toAdd) {
        if (toAdd.isEmpty()) return stacks;
        return Util.make(new ArrayList<>(stacks), (it) -> it.add(toAdd.copy()));
    }

    private static List<IItemStack> addItem(final List<IItemStack> stacks, IItemStack toAdd, int amount) {
        if (amount <= 0) return stacks;
        return Util.make(new ArrayList<>(stacks), (it) -> it.add(toAdd.copy().setAmount(amount)));
    }

    private static int binomial(final Random random, final int n, final float probability) {
        int x = 0;
        for(int i = 0; i < n; ++i) {
            if (random.nextFloat() < probability) {
                ++x;
            }
        }

        return x;
    }
    
    private static Stream<IItemStack> notEmpty(final Stream<IItemStack> stream) {
        return stream.filter(it -> !it.isEmpty());
    }

    private static Stream<IItemStack> replacing(final Stream<IItemStack> stream, final IIngredient from, final IItemStack to) {
        return notEmpty(stream.map(it -> from.matches(it)? to.copy() : it));
    }
    
    private static Stream<IItemStack> replacingExactly(final Stream<IItemStack> stream, final IItemStack from, final IItemStack to) {
        return stream.flatMap(it -> notEmpty((from.matches(it)? replacingExactly(it, from, to) : Collections.singleton(it)).stream()));
    }
    
    private static List<IItemStack> replacingExactly(final IItemStack original, final IItemStack from, final IItemStack to) {
        return Arrays.asList(to.copy().setAmount(original.getAmount() / from.getAmount()), original.copy().setAmount(original.getAmount() % from.getAmount()));
    }
}