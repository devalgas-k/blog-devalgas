import { type Ref, defineComponent, onMounted, onUnmounted, ref, watch, computed } from 'vue';
import { useI18n } from 'vue-i18n';

import { type IFooters } from '@/shared/model/footers.model';
import useDataUtils from '@/shared/data/data-utils.service';
import EntitiesMenu from '@/entities/entities-menu.vue';
import Title from '@/core/title/title.vue';
import { useRouter } from 'vue-router';
import { useStore } from '@/store.ts';
import BannerFooters from '@/core/banner/banner-footers.vue';
import MessageContactV1 from '@/entities/message/v1/message-contact/message-contact-v1.vue';
import SubscribeV1 from '@/entities/subscribe/v1/subscribe-v1.vue';
import Separator from '@/core/separator/separator.vue';

/**
 * Composant V1 de pied de page.
 * Affiche les liens, informations de contact, intégrer le formulaire de contact et l’abonnement.
 * Fournit des raccourcis vers les réseaux sociaux et actions rapides.
 */
export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'FootersV1',
  components: {
    'p-separator': Separator,
    'entities-menu': EntitiesMenu,
    'dp-title': Title,
    'banner-footers': BannerFooters,
    'message-contact-v1': MessageContactV1,
    'subscribe-v1': SubscribeV1,
  },
  setup() {
    const { t: t$ } = useI18n();
    const dataUtils = useDataUtils();

    const itemsPerPage = ref(20);
    const queryCount: Ref<number | null> = ref(null);
    const page: Ref<number> = ref(1);
    const propOrder = ref('id');
    const reverse = ref(false);
    const totalItems = ref(0);

    const footers: Ref<IFooters[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {
      page.value = 1;
    };

    const retrieveFooters = async () => {
      isFetching.value = true;
      totalItems.value = 0;
      queryCount.value = 0;
      footers.value = [];
      isFetching.value = false;
    };

    onMounted(async () => {
      await retrieveFooters();
    });

    const removeId: Ref<number | null> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: IFooters) => {
      removeId.value = instance.id ?? null;
      const modal = removeEntity.value;
      if (modal && typeof modal.show === 'function') {
        modal.show();
      }
    };
    const closeDialog = () => {
      const modal = removeEntity.value;
      if (modal && typeof modal.hide === 'function') {
        modal.hide();
      }
    };
    const removeFooters = async () => {
      closeDialog();
    };

    const changeOrder = (newOrder: string) => {
      if (propOrder.value === newOrder) {
        reverse.value = !reverse.value;
      } else {
        reverse.value = false;
      }
      propOrder.value = newOrder;
    };

    watch([propOrder, reverse], async () => {
      if (page.value === 1) {
        await retrieveFooters();
      } else {
        clear();
      }
    });

    watch(page, async (newVal, oldVal) => {
      if (newVal === oldVal) {
        return;
      }
      await retrieveFooters();
    });

    const router = useRouter();

    const navigateTo = (path: string) => {
      router.push(path);
    };

    const openMailTo = () => {
      showContactDropup.value = false;
      window.open(MAIL_TO, '_blank');
    };

    const openPhoneCall = () => {
      window.location.href = PHONE_URL;
    };

    const openSocialLink = (platform: 'linkedin' | 'twitter' | 'github' | 'medium') => {
      const socialLinks = {
        linkedin: LINKEDIN_URL,
        twitter: TWITTER_URL,
        github: GITHUB_URL,
        medium: MEDIUM_URL,
      };
      window.open(socialLinks[platform], '_blank');
    };

    const showContactDropup = ref(false);
    const toggleContactDropup = () => {
      showContactDropup.value = !showContactDropup.value;
    };
    const onContactSaved = () => {
      showContactDropup.value = false;
    };
    const openWhatsApp = () => {
      showContactDropup.value = false;
      window.open(WHATSAPP_URL, '_blank');
    };
    const separatorLabel = computed(() => t$('globalV1.separator.or').toString());

    const store = useStore();

    const authenticated = computed(() => store.authenticated);
    const writingHash = WRITING_HASH;

    const contactRef: Ref<HTMLElement | null> = ref(null);
    const onOutsideClick = (e: MouseEvent) => {
      if (showContactDropup.value) {
        const el = contactRef.value;
        if (el && !el.contains(e.target as Node)) {
          showContactDropup.value = false;
        }
      }
    };
    onMounted(() => {
      document.addEventListener('click', onOutsideClick);
    });
    onUnmounted(() => {
      document.removeEventListener('click', onOutsideClick);
    });

    return {
      footers,
      isFetching,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeFooters,
      itemsPerPage,
      queryCount,
      page,
      propOrder,
      reverse,
      totalItems,
      changeOrder,
      t$,
      ...dataUtils,

      authenticated,
      navigateTo,
      openMailTo,
      openPhoneCall,
      openSocialLink,
      showContactDropup,
      toggleContactDropup,
      onContactSaved,
      openWhatsApp,
      separatorLabel,
      writingHash,
      contactRef,
    };
  },
});
